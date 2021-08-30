package com.li.gamemanager.common.service.impl;

import com.li.gamemanager.common.entity.Role;
import com.li.gamemanager.common.model.DefaultRole;
import com.li.gamemanager.common.repository.RoleRepository;
import com.li.gamemanager.common.service.RoleReactiveService;
import com.li.gamemanager.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;

/**
 * @author li-yuanwen
 * @date 2021/6/12 18:45
 **/
@Service
@Slf4j
public class RoleReactiveServiceImpl implements RoleReactiveService {


    @Autowired
    private RoleRepository roleRepository;


    @PostConstruct
    private void init() {
        for (DefaultRole role : DefaultRole.values()) {
            String roleId = role.name().toLowerCase();
            containRole(roleId).flatMap(contain -> {
                if (contain) {
                    return Mono.empty();
                }
                // 创建默认角色
                return createRole(role.isAll() ? new Role(roleId, "all") : new Role(roleId));
            });

        }
    }


    @Override
    public Mono<Role> findById(String id) {
        return roleRepository.findById(id).flatMap(role -> {
            if (role == null) {
                return Mono.error(new IllegalArgumentException("can not find role : " + id));
            }
            return Mono.just(role);
        });
    }

    @Override
    public Mono<Void> addFunction(String roleId, String... functions) {
        return findById(roleId).flatMap(role -> {
            role.addFunctions(SecurityUtils.getCurrentUsername(), functions);
            return roleRepository.save(role).flatMap(r -> Mono.empty());
        });

    }

    private Mono<Boolean> containRole(String roleId) {
        return roleRepository.findById(roleId).flatMap(role -> role == null ? Mono.just(false) : Mono.just(true));
    }

    private Mono<Role> createRole(Role role) {
        return roleRepository.insert(role);
    }
}

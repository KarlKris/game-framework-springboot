package com.li.gamemanager.common.service.impl;

import com.li.gamemanager.common.entity.Role;
import com.li.gamemanager.common.model.DefaultRole;
import com.li.gamemanager.common.repository.RoleRepository;
import com.li.gamemanager.common.service.RoleReactiveService;
import com.li.gamemanager.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
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
        roleRepository.count().subscribe(count -> {
            if (count == null || count == 0) {
                for (DefaultRole defaultRole : DefaultRole.values()) {
                    String name = defaultRole.name().toLowerCase();
                    log.warn("新角色[{}]", defaultRole.name());
                    roleRepository.save(defaultRole.isAll() ? new Role(name, defaultRole.name()) : new Role(name)).block();
                }
                return;
            }
            log.warn("角色数量[{}]", count);
        });

    }


    @Override
    public Mono<Role> findById(String id) {
        return roleRepository.findById(id);
    }

    @Override
    public Mono<Void> addFunction(String roleId, String... functions) {
        return SecurityUtils.getCurrentUsername().flatMap(userName -> findById(roleId).flatMap(role -> {
            role.addFunctions(userName, functions);
            return roleRepository.save(role).flatMap(r -> Mono.empty());
        }));
    }

    private Mono<Role> createRole(Role role) {
        return roleRepository.save(role);
    }
}

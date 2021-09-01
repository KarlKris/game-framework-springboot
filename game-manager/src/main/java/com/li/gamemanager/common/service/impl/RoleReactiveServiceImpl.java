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
import java.util.function.Consumer;

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

        Flux.fromArray(DefaultRole.values())
                .subscribe(defaultRole
                        -> roleRepository.existsById(defaultRole.name())
                        .filter(exist -> !exist).
                                subscribe(exist -> {
                                    String name = defaultRole.name();
                                    roleRepository.save(defaultRole.isAll()
                                            ? new Role(name, defaultRole.name()) : new Role(name))
                                            .subscribe(role -> log.warn("新角色[{}]", role.getRole()));
                                }));

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

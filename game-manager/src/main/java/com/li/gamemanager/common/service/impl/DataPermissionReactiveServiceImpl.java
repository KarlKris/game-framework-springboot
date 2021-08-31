package com.li.gamemanager.common.service.impl;

import com.li.gamemanager.common.entity.DataPermission;
import com.li.gamemanager.common.repository.DataPermissionRepository;
import com.li.gamemanager.common.service.DataPermissionReactiveService;
import com.li.gamemanager.common.service.RoleReactiveService;
import com.li.gamemanager.common.service.UserReactiveService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

/**
 * @author li-yuanwen
 */
@Slf4j
@Service
public class DataPermissionReactiveServiceImpl implements DataPermissionReactiveService {

    @Autowired
    private DataPermissionRepository dataPermissionRepository;
    @Autowired
    private UserReactiveService userReactiveService;
    @Autowired
    private RoleReactiveService roleReactiveService;

    @Override
    public Mono<List<DataPermission>> findByUser(String userName) {
        return userReactiveService.findByName(userName).flatMap(user -> {
            if (user == null) {
                return Mono.error(new IllegalArgumentException("can not find user by userName:" + userName));
            }
            return roleReactiveService.findById(user.getRole()).flatMap(role -> {
                List<DataPermission> list = new ArrayList<>(role.getDataPermissions().size());
                for (String dataPermission : role.getDataPermissions()) {
                    findById(dataPermission).flatMap(dp -> {
                        list.add(dp);
                        return Mono.empty();
                    });
                }
                return Mono.just(list);
            });
        });
    }

    @Override
    public Mono<DataPermission> findById(String id) {
        return dataPermissionRepository.findById(id).flatMap(dataPermission -> {
            if (dataPermission == null) {
                return Mono.error(new IllegalArgumentException("can not find dataPermission:" + id));
            }
            return Mono.just(dataPermission);
        });
    }

    @Override
    public Mono<Void> update(DataPermission dataPermission) {
        return dataPermissionRepository.save(dataPermission).flatMap(dp -> Mono.empty());
    }

    @Override
    public Mono<DataPermission> addDataPermission(DataPermission dataPermission) {
        return dataPermissionRepository.insert(dataPermission);
    }
}

package com.li.gamemanager.common.service.impl;

import com.li.gamemanager.common.entity.Role;
import com.li.gamemanager.common.model.DefaultRole;
import com.li.gamemanager.common.repository.RoleRepository;
import com.li.gamemanager.common.service.RoleService;
import com.li.gamemanager.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Optional;

/**
 * @author li-yuanwen
 * @date 2021/6/12 18:45
 **/
@Service
@Slf4j
public class RoleServiceImpl implements RoleService {


    @Autowired
    private RoleRepository roleRepository;

    @PostConstruct
    private void init() {
        for (DefaultRole role : DefaultRole.values()) {
            String roleId = role.name().toLowerCase();
            if (containRole(roleId)) {
                continue;
            }
            // 创建默认角色
            createRole(role.isAll() ? new Role(roleId, "all") : new Role(roleId));
        }
    }


    @Override
    public Role findById(String id) {
        Optional<Role> optional = roleRepository.findById(id);
        if (optional.isPresent()) {
            return optional.get();
        }
        throw new IllegalArgumentException("can not find role : " + id);
    }

    @Override
    public void addFunction(String roleId, String... functions) {
        Role role = findById(roleId);
        role.addFunctions(SecurityUtils.getCurrentUsername(), functions);
    }

    private boolean containRole(String roleId) {
        return roleRepository.findById(roleId).isPresent();
    }

    private void createRole(Role role) {
        roleRepository.insert(role);
    }
}

package com.li.gamemanager.common.service.impl;

import com.li.gamemanager.common.entity.DataPermission;
import com.li.gamemanager.common.entity.Role;
import com.li.gamemanager.common.entity.User;
import com.li.gamemanager.common.repository.DataPermissionRepository;
import com.li.gamemanager.common.service.DataPermissionService;
import com.li.gamemanager.common.service.RoleService;
import com.li.gamemanager.common.service.UserService;
import com.li.gamemanager.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author li-yuanwen
 */
@Slf4j
@Service
public class DataPermissionServiceImpl implements DataPermissionService {

    @Autowired
    private DataPermissionRepository dataPermissionRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private RoleService roleService;

    @Override
    public boolean check(String... permissions) {
        // 获取当前用户的所有权限
        List<String> elPermissions = SecurityUtils.getCurrentUser()
                .getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        // 判断当前用户的所有权限是否包含接口上定义的权限
        return elPermissions.contains("all") || Arrays.stream(permissions).anyMatch(elPermissions::contains);
    }

    @Override
    public List<DataPermission> findByUser(String userName) {
        User user = userService.findByName(userName);
        if (user == null) {
            throw new IllegalArgumentException("can not find user by userName:" + userName);
        }
        Role role = roleService.findById(user.getRole());
        List<DataPermission> list = new ArrayList<>(role.getDataPermissions().size());
        for (String dataPermission : role.getDataPermissions()) {
            list.add(findById(dataPermission));
        }
        return list;
    }

    @Override
    public DataPermission findById(String id) {
        Optional<DataPermission> optional = dataPermissionRepository.findById(id);
        if (optional.isPresent()) {
            return optional.get();
        }
        throw new IllegalArgumentException("can not find dataPermission:" + id);
    }

    @Override
    public void update(DataPermission dataPermission) {
        dataPermissionRepository.save(dataPermission);
    }

    @Override
    public void addDataPermission(DataPermission dataPermission) {
        dataPermissionRepository.insert(dataPermission);
    }
}

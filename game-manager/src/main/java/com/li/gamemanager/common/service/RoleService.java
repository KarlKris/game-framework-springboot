package com.li.gamemanager.common.service;


import com.li.gamemanager.common.entity.Role;

/**
 * 角色Service
 * @author li-yuanwen
 */
public interface RoleService {


    /**
     * 查询角色
     * @param id 角色id
     * @return /
     */
    Role findById(String id);

    /**
     * 添加功能权限
     * @param roleId 角色id
     * @param functions 功能列表
     */
    void addFunction(String roleId, String... functions);


}

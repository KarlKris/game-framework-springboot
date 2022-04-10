package com.li.manager.common.service;


import com.li.manager.common.entity.Role;
import reactor.core.publisher.Mono;

/**
 * 角色Service
 * @author li-yuanwen
 */
public interface RoleReactiveService {


    /**
     * 查询角色
     * @param id 角色id
     * @return /
     */
    Mono<Role> findById(String id);

    /**
     * 添加功能权限
     * @param roleId 角色id
     * @param functions 功能列表
     */
    Mono<Void> addFunction(String roleId, String... functions);


}

package com.li.manager.common.service;


import com.li.manager.common.entity.DataPermission;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * @author li-yuanwen
 * 数据权限接口
 */
public interface DataPermissionReactiveService {

    /**
     * 查询权限
     * @param userName 用户名称
     * @return /
     */
    Mono<List<DataPermission>> findByUser(String userName);

    /**
     * 查询权限
     * @param id 权限id
     * @return
     */
    Mono<DataPermission> findById(String id);

    /**
     * 更新权限
     * @param dataPermission 权限
     */
    Mono<Void> update(DataPermission dataPermission);

    /**
     * 新增权限
     * @param dataPermission 权限
     */
    Mono<DataPermission> addDataPermission(DataPermission dataPermission);

}

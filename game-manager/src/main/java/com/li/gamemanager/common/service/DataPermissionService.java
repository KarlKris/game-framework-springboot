package com.li.gamemanager.common.service;


import com.li.gamemanager.common.entity.DataPermission;
import java.util.List;

/**
 * @author li-yuanwen
 * 数据权限接口
 */
public interface DataPermissionService {


    /**
     * 检查当前用户是否拥有权限
     * @param permissions 权限
     * @return true　拥有
     */
    boolean check(String ...permissions);

    /**
     * 查询权限
     * @param userName 用户名称
     * @return /
     */
    List<DataPermission> findByUser(String userName);

    /**
     * 查询权限
     * @param id 权限id
     * @return
     */
    DataPermission findById(String id);

    /**
     * 更新权限
     * @param dataPermission 权限
     */
    void update(DataPermission dataPermission);

    /**
     * 新增权限
     * @param dataPermission 权限
     */
    void addDataPermission(DataPermission dataPermission);

}

package com.li.manager.common.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * @author li-yuanwen
 * 角色
 */
@Document
@Getter
@NoArgsConstructor
public class Role {

    /** 角色 **/
    @Id
    private String role;

    /** 拥有功能权限列表 **/
    private Set<String> functionPermissions;

    /** 拥有数据权限列表 **/
    private Set<String> dataPermissions;

    /** 创建用户 **/
    private String createUserName;

    /** 创建时间 **/
    private Date createDate;

    /** 更新用户 **/
    private String updateUserName;

    /** 更新时间 **/
    private Date updateDate;

    public Role(String roleId, String functionPermissions) {
        this.role = roleId;
        this.functionPermissions = new HashSet<>(1);
        this.functionPermissions.add(functionPermissions);
        this.dataPermissions = new HashSet<>();
        this.createDate = new Date();
    }

    public Role(String roleId) {
        this.role = roleId;
        this.functionPermissions = new HashSet<>(1);
        this.dataPermissions = new HashSet<>(1);
        this.createDate = new Date();
    }

    public void addFunctions(String updateUserName, String... functions) {
        this.updateUserName = updateUserName;
        this.updateDate = new Date();
        functionPermissions.addAll(Arrays.asList(functions));
    }

}

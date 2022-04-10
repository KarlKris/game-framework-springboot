package com.li.manager.common.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * @author li-yuanwen
 * 管理后台用户
 */
@Document
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class User {

    /** 用户名 **/
    @Id
    private String userName;

    /** 密码 **/
    private String password;

    /** 用户角色 **/
    private String role;

    /** 头像真实名称 **/
    private String avatarName;

    /** 头像存储的路径 **/
    private String avatarPath;

    /** 是否启用 **/
    private Boolean enabled;

    /** 创建时间 **/
    private Date createTime;

    /** 创建玩家 **/
    private String createUserName;

    public void changePwd(String password) {
        this.password = password;
    }

    public User(String userName, String pwd, String role) {
        this.userName = userName;
        this.password = pwd;
        this.role = role;
        this.avatarName = "avatar.png";
        this.avatarPath = "C:\\avatar\\avatar-101010.png";
        this.enabled = true;
    }

    public void setAvatarName(String avatarName) {
        this.avatarName = avatarName;
    }

    public void setAvatarPath(String avatarPath) {
        this.avatarPath = avatarPath;
    }

}

package com.li.gamemanager.common.model;

/**
 * @author li-yuanwen
 * @date 2021/6/12 18:25
 **/
public enum DefaultUser {

    /** 管理员账号 **/
    ADMIN("123456", "admin"),

    ;

    /** 密码 **/
    private String pwd;
    /** 角色 **/
    private String role;

    public String getPwd() {
        return pwd;
    }

    public String getRole() {
        return role;
    }

    DefaultUser(String pwd, String role) {
        this.pwd = pwd;
        this.role = role;
    }
}

package com.li.gamemanager.common.model;

/**
 * @author li-yuanwen
 * @date 2021/6/12 18:27
 **/
public enum DefaultRole {

    /** 管理员角色 **/
    ADMIN(true),

    ;

    /** 是否拥有所有权限 **/
    private boolean all;

    public boolean isAll() {
        return all;
    }

    DefaultRole(boolean all) {
        this.all = all;
    }

}

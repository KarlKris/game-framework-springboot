package com.li.gamecluster.zookeeper.model;

import com.li.gamecluster.zookeeper.selector.ServiceSelector;

/**
 * @author li-yuanwen
 * @date 2021/8/7 20:17
 * 服务类型
 **/
public enum ServerType {

    /** 网关服 **/
    GATEWAY("Gateway", ServiceSelector.BALANCE_SELECTOR),

    /** 游戏服 **/
    GAME_SERVER("Game_Server", ServiceSelector.IDENTITY_SELECTOR),

    ;

    /** 服务名 **/
    private String serviceName;
    /** 选择器 **/
    private ServiceSelector selector;

    ServerType(String serviceName, ServiceSelector selector){
        this.serviceName = serviceName;
        this.selector = selector;
    }

    public String getServiceName() {
        return serviceName;
    }

    public ServiceSelector getSelector() {
        return selector;
    }



}

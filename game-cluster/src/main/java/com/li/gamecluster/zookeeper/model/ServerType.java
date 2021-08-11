package com.li.gamecluster.zookeeper.model;

import com.li.gamecluster.zookeeper.selector.ServiceSelector;

/**
 * @author li-yuanwen
 * @date 2021/8/7 20:17
 * 服务类型
 **/
public enum ServerType {

    /** 网关服 **/
    GATEWAY(ServiceSelector.BALANCE_SELECTOR),

    /** 游戏服 **/
    GAME_SERVER(ServiceSelector.IDENTITY_SELECTOR),

    ;

    /** 选择器 **/
    private ServiceSelector selector;

    ServerType(ServiceSelector selector){
        this.selector = selector;
    }


    public ServiceSelector getSelector() {
        return selector;
    }



}

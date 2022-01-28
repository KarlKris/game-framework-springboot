package com.li.gamecluster.zookeeper.model;

import com.li.gamecluster.zookeeper.selector.ServerSelector;

/**
 * @author li-yuanwen
 * @date 2021/8/7 20:17
 * 服务类型
 **/
public enum ServerType {

    /** 网关服 **/
    GATEWAY(ServerSelector.BALANCE_SELECTOR),

    /** 游戏服 **/
    GAME_SERVER(ServerSelector.IDENTITY_SELECTOR),

    ;

    /** 选择器 **/
    private final ServerSelector selector;

    ServerType(ServerSelector selector){
        this.selector = selector;
    }


    public ServerSelector getSelector() {
        return selector;
    }



}

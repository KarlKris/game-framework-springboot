package com.li.gamecore.eventbus.event;

/**
 * 玩家事件
 * @author li-yuanwen
 * @date 2022/3/10
 */
public interface IdentityEvent {


    /**
     * 获取玩家标识
     * @return 玩家标识
     */
    Long getIdentity();


}

package com.li.battle.event.model;

/**
 * 战斗相关事件
 * @author li-yuanwen
 * @date 2021/10/18
 */
public interface BattleEvent<T> {

    /**
     * 获取战斗类型
     * @return 战斗类型
     */
    BattleEventType getType();

    /**
     * 获取战斗事件内容
     * @return 战斗事件内容
     */
    T getEventBody();


}

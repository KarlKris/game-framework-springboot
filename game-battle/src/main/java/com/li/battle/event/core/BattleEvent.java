package com.li.battle.event.core;

/**
 * 战斗事件
 * @author li-yuanwen
 * @date 2022/5/18
 */
public interface BattleEvent {


    /**
     * 事件类型
     * @return 事件类型
     */
    BattleEventType getType();


    /**
     * 事件来源标识
     * @return 事件来源标识
     */
    long getSource();


}

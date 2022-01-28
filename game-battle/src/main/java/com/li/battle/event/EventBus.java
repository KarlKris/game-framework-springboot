package com.li.battle.event;

import com.li.battle.event.model.BattleEvent;

/**
 * 战斗相关事件总线
 * @author li-yuanwen
 * @date 2021/10/18
 */
public interface EventBus {


    /**
     * 抛出事件
     * @param event 事件内容
     */
    void post(BattleEvent<?> event);

}

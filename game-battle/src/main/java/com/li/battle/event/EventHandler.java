package com.li.battle.event;

import com.li.battle.event.model.BattleEvent;
import com.li.battle.event.model.BattleEventType;

/**
 * 事件处理器
 * @author li-yuanwen
 * @date 2022/5/20
 */
public interface EventHandler<E extends BattleEvent> {


    /**
     * 负责的事件类型
     * @return 事件类型
     */
    BattleEventType getEventType();


    /**
     * 事件处理
     * @param event 事件内容
     */
    void handle(E event);

}

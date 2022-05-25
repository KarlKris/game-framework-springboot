package com.li.battle.event.handler;

import com.li.battle.event.EventHandlerContext;
import com.li.battle.event.core.BattleEventType;

/**
 * 事件处理器
 * @author li-yuanwen
 * @date 2022/5/20
 */
public interface EventHandler {


    /**
     * 负责的事件类型
     * @return 事件类型
     */
    BattleEventType getEventType();


    /**
     * 事件处理
     * @param context 事件责任链
     * @param receiver 事件处理者
     * @param event 事件内容
     */
    void handle(EventHandlerContext context, Object receiver, Object event);


}

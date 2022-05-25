package com.li.battle.event;

import com.li.battle.event.handler.EventHandler;
import com.li.battle.event.core.BattleEventType;

import java.util.List;

/**
 * 事件责任链
 * @author li-yuanwen
 * @date 2022/5/23
 */
public interface EventPipeline {


    /**
     * 返回事件接收者
     * @return 事件接收者
     */
    EventReceiver eventReceiver();


    /**
     * 获取责任链内所有的事件类型
     * @return 所有的事件类型
     */
    List<BattleEventType> getEventTypes();


    /**
     * 添加事件处理器
     * @param handler 事件处理器
     * @return 事件责任链
     */
    EventPipeline addHandler(EventHandler handler);


    /**
     * 责任链内第一个EventHandlerContext
     * @return 第一个EventHandlerContext
     */
    EventHandlerContext firstEventHandlerContext();

}

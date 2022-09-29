package com.li.battle.buff.handler;

import com.li.battle.buff.core.Buff;
import com.li.battle.event.EventHandlerContext;
import com.li.battle.event.core.BattleEvent;
import com.li.battle.event.handler.AbstractEventHandler;

/**
 * buff类事件处理器基类  // todo 后续考虑效果的执行频率问题,需不需要在buff期间考虑cd问题
 * @author li-yuanwen
 * @date 2022/5/23
 */
public abstract class AbstractBuffHandler<B extends Buff, E extends BattleEvent> extends AbstractEventHandler<B, E> {

    @Override
    protected void handle0(EventHandlerContext context, B receiver, E event) {
        if (receiver.isManualExpire()) {
            return;
        }

        doHandle(context, receiver, event);
    }

    /**
     * 留给子类去实现具体逻辑
     * @param context 事件责任链
     * @param receiver 事件接收者
     * @param event 事件内容
     */
    protected abstract void doHandle(EventHandlerContext context, B receiver, E event);
}

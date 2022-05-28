package com.li.battle.event.handler;

import com.li.battle.event.EventHandlerContext;
import com.li.battle.event.EventReceiver;
import com.li.battle.event.core.BattleEvent;
import com.li.common.utils.TypeParameterMatcher;

/**
 * 抽象的事件处理器,增加了事件内容和事件接受者的类型判断
 * @author li-yuanwen
 * @date 2022/5/23
 */
public abstract class AbstractEventHandler<I extends EventReceiver, E extends BattleEvent> implements EventHandler {

    /** 事件接收者类型比对器 **/
    private final TypeParameterMatcher receiverMatcher;
    /** 事件内容类型比对器 **/
    private final TypeParameterMatcher eventMatcher;

    public AbstractEventHandler() {
        this.receiverMatcher = TypeParameterMatcher.find(this, AbstractEventHandler.class, "I");
        this.eventMatcher = TypeParameterMatcher.find(this, AbstractEventHandler.class, "E");
    }

    @Override
    public void handle(EventHandlerContext context, Object receiver, Object event) {
        if (accept(receiver, event)) {
            I castReceiver = (I) receiver;
            E castEvent = (E) event;
            handle0(context, castReceiver, castEvent);
        }

        context.fireHandleEvent(event);
    }

    /**
     * 实际对事件内容的处理
     * @param context 事件责任链
     * @param receiver 事件接收者
     * @param event 事件内容
     */
    protected abstract void handle0(EventHandlerContext context, I receiver, E event);


    protected boolean accept(Object receiver, Object event) {
        return receiverMatcher.match(receiver) && eventMatcher.match(event);
    }
}

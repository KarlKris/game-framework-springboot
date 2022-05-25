package com.li.battle.event;

/**
 * EventHandlerContext基类
 * @author li-yuanwen
 * @date 2022/5/23
 */
public abstract class AbstractEventHandlerContext implements EventHandlerContext {

    /** 事件责任链 **/
    private final EventPipeline pipeline;
    /** 下一个EventHandlerContext实例 **/
    AbstractEventHandlerContext next;

    public AbstractEventHandlerContext(EventPipeline pipeline) {
        this.pipeline = pipeline;
    }

    @Override
    public EventReceiver eventReceiver() {
        return pipeline.eventReceiver();
    }

    @Override
    public EventPipeline eventPipeline() {
        return pipeline;
    }

    @Override
    public void fireHandleEvent(Object event) {
        AbstractEventHandlerContext ctx = this.next;
        ctx.invokeHandler(event);
    }

    private void invokeHandler(Object event) {
        eventHandler().handle(this, eventReceiver(), event);
    }


}

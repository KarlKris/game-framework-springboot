package com.li.battle.event;

/**
 * 事件接受者接口
 * @author li-yuanwen
 * @date 2022/5/23
 */
public interface EventReceiver {


    /**
     * 返回事件接收者的责任链
     * @return 事件接收者的责任链
     */
    default EventPipeline eventPipeline() {
        return new DefaultEventPipeline(this);
    }


    /**
     * 事件接收者是否有效
     * @param curRound 当前回合数
     * @return false 事件接收者已无效
     */
    boolean isValid(long curRound);


    /**
     * 向EventDispatcher注册自身
     */
    void registerEventReceiverIfNecessary();

}

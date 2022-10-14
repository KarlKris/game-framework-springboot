package com.li.battle.event;

import com.li.battle.core.IOwner;

/**
 * 事件接受者接口, EventReceiver持有者是场景内的某个单位
 * @author li-yuanwen
 * @date 2022/5/23
 */
public interface EventReceiver extends IOwner {


    /**
     * 返回事件接收者的责任链
     * @return 事件接收者的责任链
     */
    default EventPipeline newEventPipeline() {
        return new DefaultEventPipeline(this);
    }

    /**
     * 手动无效
     */
    void expire();

    /**
     * 事件接收者是否有效
     * @return true 事件接收者已无效
     */
    boolean isExpire();


    /**
     * 向EventDispatcher注册自身
     */
    void registerEventReceiverIfNecessary();

}

package com.li.battle.trigger.core;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.li.battle.trigger.model.TriggerType;

/**
 * 触发器接口
 * @author li-yuanwen
 * @date 2022/5/17
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type")
public interface Trigger {

    /**
     * 触发器类型
     * @return 触发器类型
     */
    TriggerType getType();

    /**
     * 尝试触发
     * @return true 触发
     */
    boolean tryTrigger();

    /**
     * 判断触发器是否过期
     * @return true 过期
     */
    boolean isTimeOut();

    /**
     * 复制触发器
     * @return 触发器副本
     */
    Trigger copy();


}

package com.li.battle.trigger.region;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * 触发器范围接口
 * @author li-yuanwen
 * @date 2022/5/18
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type")
public interface TriggerRegion {


    /**
     * 判断是否在触发器范围内
     * @return true 在触发器范围内
     */
    boolean isInRegion();


}

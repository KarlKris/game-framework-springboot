package com.li.battle.trigger.domain;

import com.fasterxml.jackson.annotation.*;
import com.li.battle.trigger.TriggerType;

/**
 * 触发器参数
 * @author li-yuanwen
 * @date 2022/9/26
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = DetonateTriggerParam.class, name = TriggerParam.FIX_TARGET_DETONATE),
        @JsonSubTypes.Type(value = DetonateTriggerParam.class, name = TriggerParam.FIX_CASTER_DETONATE),
        @JsonSubTypes.Type(value = DistanceTriggerParam.class, name = TriggerParam.DISTANCE),
        @JsonSubTypes.Type(value = DetonateTriggerParam.class, name = TriggerParam.TARGET_DETONATE),
})
public interface TriggerParam {

    String FIX_TARGET_DETONATE = "FIX_TARGET_DETONATE";
    String FIX_CASTER_DETONATE = "FIX_CASTER_DETONATE";
    String DISTANCE = "DISTANCE";
    String TARGET_DETONATE = "TARGET_DETONATE";

    /**
     * 触发器类型
     * @return 触发器类型
     */
    TriggerType getType();

}

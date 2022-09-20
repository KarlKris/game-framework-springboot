package com.li.battle.trigger.core;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.li.battle.event.core.BattleEvent;
import com.li.battle.trigger.TriggerType;

/**
 * 触发器接口
 * @author li-yuanwen
 * @date 2022/5/17
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = FixTargetDetonateTrigger.class, name = Trigger.FIX_TARGET_DETONATE),
        @JsonSubTypes.Type(value = FixCasterDetonateTrigger.class, name = Trigger.FIX_CASTER_DETONATE),
})
public interface Trigger {

    String FIX_TARGET_DETONATE = "FIX_TARGET_DETONATE";
    String FIX_CASTER_DETONATE = "FIX_CASTER_DETONATE";


    /**
     * 触发器类型
     * @return 触发器类型
     */
    TriggerType getType();

    /**
     * 尝试触发
     * @param casterId 触发器释放方唯一标识
     * @param event 事件内容
     * @param callback 触发成功后执行的回调
     */
    void tryTrigger(long casterId, BattleEvent event, TriggerSuccessCallback callback);

    /**
     * 复制触发器
     * @return 触发器副本
     */
    Trigger copy();


}

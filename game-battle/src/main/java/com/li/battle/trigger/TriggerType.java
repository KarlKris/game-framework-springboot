package com.li.battle.trigger;

import com.li.battle.trigger.handler.*;
import lombok.Getter;

/**
 * 触发器类型
 * @author li-yuanwen
 * @date 2022/5/20
 */
@Getter
public enum TriggerType {

    /** 固定技能目标引爆类触发器 **/
    FIX_TARGET_DETONATE(FixTargetDetonateTriggerHandler.class),
    /** 固定技能施法方引爆类触发器 **/
    FIX_CASTER_DETONATE(FixCasterDetonateTriggerHandler.class),
    /** 技能目标引爆类触发器 **/
    TARGET_DETONATE(TargetDetonateTriggerHandler.class),
    /** 双方间距触发器 **/
    DISTANCE(DistanceTriggerHandler.class),

    ;

    /** 触发器监听的事件集 **/
    private final Class<? extends AbstractTriggerHandler<?, ?>>[] handlersClz;

    @SafeVarargs
    TriggerType(Class<? extends AbstractTriggerHandler<?, ?>>... handlersClz) {
        this.handlersClz = handlersClz;
    }
}

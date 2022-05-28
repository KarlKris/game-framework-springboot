package com.li.battle.trigger;

import com.li.battle.trigger.core.FixCasterDetonateTrigger;
import com.li.battle.trigger.core.FixTargetDetonateTrigger;
import com.li.battle.trigger.core.Trigger;
import com.li.battle.trigger.handler.AbstractTriggerHandler;
import com.li.battle.trigger.handler.SkillExecutedTriggerHandler;
import lombok.Getter;

/**
 * 触发器类型
 * @author li-yuanwen
 * @date 2022/5/20
 */
@Getter
public enum TriggerType {

    /** 技能目标固定引爆类触发器 **/
    FIX_TARGET_DETONATE(FixTargetDetonateTrigger.class, SkillExecutedTriggerHandler.class),
    /** 技能施法方固定引爆类触发器 **/
    FIX_CASTER_DETONATE(FixCasterDetonateTrigger.class, SkillExecutedTriggerHandler.class),

    ;

    /** 触发器类型 **/
    private final Class<? extends Trigger> clz;
    /** 触发器监听的事件集 **/
    private final Class<? extends AbstractTriggerHandler<?>>[] handlersClz;

    TriggerType(Class<? extends Trigger> clz, Class<? extends AbstractTriggerHandler<?>>... handlersClz) {
        this.clz = clz;
        this.handlersClz = handlersClz;
    }
}

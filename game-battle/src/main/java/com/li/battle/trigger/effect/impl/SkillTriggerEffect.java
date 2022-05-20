package com.li.battle.trigger.effect.impl;

import com.li.battle.effect.Effect;
import com.li.battle.trigger.core.Trigger;
import com.li.battle.trigger.effect.AbstractTriggerEffect;

/**
 * 技能触发器效果
 * @author li-yuanwen
 * @date 2022/5/18
 */
public class SkillTriggerEffect extends AbstractTriggerEffect {

    /** 技能id **/
    private final int skillId;

    public SkillTriggerEffect(int skillId, long source, Trigger trigger, Effect[] effects) {
        super(source, trigger, effects);
        this.skillId = skillId;
    }
}

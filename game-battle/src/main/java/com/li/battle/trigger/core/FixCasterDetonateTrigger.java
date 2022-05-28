package com.li.battle.trigger.core;

import com.li.battle.event.core.SkillExecutedEvent;
import com.li.battle.trigger.TriggerType;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 固定施法引爆型Trigger
 * @author li-yuanwen
 * @date 2022/5/27
 */
@Getter
@NoArgsConstructor
public class FixCasterDetonateTrigger extends AbstractDetonateTrigger {

    public FixCasterDetonateTrigger(int[] skillIds, int num) {
        super(skillIds, num);
    }

    @Override
    protected void try0(long casterId, long target, SkillExecutedEvent event, TriggerSuccessCallback callback) {
        // 引爆成功
        if (++curNum >= num) {
            // 自身的引爆目标
            callback.callback(casterId);
        }
    }

    @Override
    public TriggerType getType() {
        return TriggerType.FIX_CASTER_DETONATE;
    }

    @Override
    public Trigger copy() {
        return new FixCasterDetonateTrigger(skillIds, num);
    }
}

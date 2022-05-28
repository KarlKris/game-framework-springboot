package com.li.battle.trigger.core;

import com.li.battle.core.unit.FightUnit;
import com.li.battle.core.unit.IPosition;
import com.li.battle.event.core.SkillExecutedEvent;
import com.li.battle.selector.SelectorResult;
import com.li.battle.trigger.TriggerType;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 固定目标引爆型Trigger
 * @author li-yuanwen
 * @date 2022/5/27
 */
@Getter
@NoArgsConstructor
public class FixTargetDetonateTrigger extends AbstractDetonateTrigger {

    public FixTargetDetonateTrigger(int[] skillIds, int num) {
        super(skillIds, num);
    }

    @Override
    public TriggerType getType() {
        return TriggerType.FIX_TARGET_DETONATE;
    }

    @Override
    protected void try0(long casterId, long target, SkillExecutedEvent event, TriggerSuccessCallback callback) {
        // 查看挂载目标
        SelectorResult selectorResult = event.getSkill().getTarget();
        for (IPosition position : selectorResult.getResults()) {
            if (!(position instanceof FightUnit)) {
                continue;
            }
            FightUnit unit = (FightUnit) position;
            if (unit.getId() != target) {
                continue;
            }
            // 引爆成功
            if (++curNum >= num) {
                callback.callback(unit.getId());
            }
        }
    }

    @Override
    public Trigger copy() {
        return new FixTargetDetonateTrigger(skillIds, num);
    }
}

package com.li.battle.trigger.handler;

import com.li.battle.core.unit.*;
import com.li.battle.event.core.*;
import com.li.battle.trigger.core.FixTargetDetonateTrigger;
import com.li.battle.trigger.domain.DetonateTriggerParam;
import org.springframework.stereotype.Component;

/**
 * 技能目标固定引爆类触发器监听技能执行事件
 * @author li-yuanwen
 * @date 2022/9/26
 */
@Component
public class FixTargetDetonateTriggerHandler extends AbstractTriggerHandler<FixTargetDetonateTrigger, SkillExecutedEvent> {

    @Override
    public BattleEventType getEventType() {
        return BattleEventType.SKILL_EXECUTED;
    }

    @Override
    protected void doHandle(FixTargetDetonateTrigger receiver, SkillExecutedEvent event) {
        DetonateTriggerParam param = receiver.getConfig().getParam();
        for (IPosition position : event.getSkill().getFinalTargets()) {
            if (!(position instanceof FightUnit)) {
                continue;
            }
            FightUnit unit = (FightUnit) position;
            if (unit.getId() != receiver.getParent()) {
                continue;
            }
            if (receiver.increment() >= param.getNum()) {
                executeEffect(receiver, unit);
            }
            break;
        }
    }
}

package com.li.battle.trigger.handler;

import com.li.battle.core.unit.FightUnit;
import com.li.battle.event.core.*;
import com.li.battle.trigger.core.FixCasterDetonateTrigger;
import com.li.battle.trigger.domain.DetonateTriggerParam;
import org.springframework.stereotype.Component;

/**
 * 技能施法方固定引爆类触发器监听技能执行事件
 * @author li-yuanwen
 * @date 2022/9/26
 */
@Component
public class FixCasterDetonateTriggerHandler extends AbstractTriggerHandler<FixCasterDetonateTrigger, SkillExecutedEvent> {

    @Override
    public BattleEventType getEventType() {
        return BattleEventType.SKILL_EXECUTED;
    }

    @Override
    protected void doHandle(FixCasterDetonateTrigger receiver, SkillExecutedEvent event) {
        DetonateTriggerParam param = receiver.getConfig().getParam();
        if (receiver.increment() >= param.getNum()) {
            FightUnit unit = receiver.battleScene().getFightUnit(event.getSource());
            executeEffect(receiver, unit);
        }
    }
}

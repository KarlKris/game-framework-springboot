package com.li.battle.trigger.handler;

import com.li.battle.event.core.BattleEventType;
import com.li.battle.event.core.SkillExecutedEvent;
import org.springframework.stereotype.Component;

/**
 * Trigger监听技能执行的处理器
 * @author li-yuanwen
 * @date 2022/5/27
 */
@Component
public class SkillExecutedTriggerHandler extends AbstractTriggerHandler<SkillExecutedEvent> {

    @Override
    public BattleEventType getEventType() {
        return BattleEventType.SKILL_EXECUTED;
    }
}

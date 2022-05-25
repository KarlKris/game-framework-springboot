package com.li.battle.event.core;

import com.li.battle.skill.BattleSkill;
import lombok.Getter;

/**
 * 某个主动技能执行事件
 * @author li-yuanwen
 * @date 2022/5/23
 */
@Getter
public class SkillExecutedEvent extends AbstractSkillEvent {

    public SkillExecutedEvent(BattleSkill skill) {
        super(skill);
    }

    @Override
    public BattleEventType getType() {
        return BattleEventType.SKILL_EXECUTED;
    }

}

package com.li.battle.event.core;

import com.li.battle.skill.BattleSkill;

/**
 * 技能相关事件基类
 * @author li-yuanwen
 * @date 2022/5/23
 */
public abstract class AbstractSkillEvent implements BattleEvent {

    /** 技能 **/
    private final BattleSkill skill;

    public AbstractSkillEvent(BattleSkill skill) {
        this.skill = skill;
    }

    @Override
    public long getSource() {
        return skill.getCaster();
    }

    public BattleSkill getSkill() {
        return skill;
    }
}

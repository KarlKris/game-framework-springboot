package com.li.battle.core.unit.model;

import lombok.Getter;

/**
 * 战斗单元战斗技能
 * @author li-yuanwen
 * @date 2021/10/19
 */
@Getter
public class BattleSkill {

    /** 技能id **/
    private final int skillId;

    /** 技能等级 **/
    private final int level;

    public BattleSkill(int skillId, int level) {
        this.skillId = skillId;
        this.level = level;
    }

}

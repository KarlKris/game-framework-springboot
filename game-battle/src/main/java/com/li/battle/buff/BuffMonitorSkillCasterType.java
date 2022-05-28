package com.li.battle.buff;

import com.li.battle.buff.core.Buff;
import com.li.battle.skill.BattleSkill;

import java.util.function.BiFunction;

/**
 * buff监听主动技能释放人类型
 * @author li-yuanwen
 * @date 2022/5/26
 */
public enum BuffMonitorSkillCasterType {

    /** 自己释放 **/
    SELF((buff, battleSkill) -> buff.getParent() == battleSkill.getCaster()),


    ;

    private final BiFunction<Buff, BattleSkill, Boolean> function;

    BuffMonitorSkillCasterType(BiFunction<Buff, BattleSkill, Boolean> function) {
        this.function = function;
    }

    public boolean isMatchCasterCondition(Buff buff, BattleSkill skill) {
        return function.apply(buff, skill);
    }
}

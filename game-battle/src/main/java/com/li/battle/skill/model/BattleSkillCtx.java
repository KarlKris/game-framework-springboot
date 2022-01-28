package com.li.battle.skill.model;

import com.li.battle.core.unit.model.BattleSkill;
import com.li.battle.selector.core.BattleTarget;
import lombok.Getter;

/**
 * 场景中存在的技能上下文
 * @author li-yuanwen
 * @date 2021/10/20
 */
@Getter
public class BattleSkillCtx {

    /** 技能信息 **/
    private BattleSkill skill;
    /** 释放战斗单元标识 **/
    private long caster;
    /** 目标 **/
    private BattleTarget target;
    /** 下一次执行技能效果的回合 **/
    private long nextRound;
    /** 技能失效的回合 **/
    private long expireRound;
}

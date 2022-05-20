package com.li.battle.skill.model;

import com.li.battle.selector.core.SelectorTarget;
import lombok.Getter;

/**
 * 场景中存在的技能上下文
 * @author li-yuanwen
 * @date 2021/10/20
 */
@Getter
public class BattleSkill {

    /** 技能配置 **/
    private int skillId;
    /** 释放战斗单元标识 **/
    private long caster;
    /** 下次执行的技能阶段 **/
    private SkillStage nextStage = SkillStage.START;
    /** 目标 **/
    private SelectorTarget target;
    /** 下一次执行技能效果的回合 **/
    private long nextRound;
    /** 技能失效的回合 **/
    private long expireRound;


    public void updateSkillStage(SkillStage nextStage) {
        this.nextStage = nextStage;
    }


}

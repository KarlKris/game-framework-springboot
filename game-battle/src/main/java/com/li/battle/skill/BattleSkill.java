package com.li.battle.skill;

import com.li.battle.core.IOwner;
import com.li.battle.core.context.AbstractDamageAlterContext;
import com.li.battle.selector.SelectParam;
import com.li.battle.selector.SelectorResult;
import lombok.Getter;

/**
 * 场景中存在的技能上下文
 * @author li-yuanwen
 * @date 2021/10/20
 */
@Getter
public class BattleSkill implements IOwner {

    /** 技能配置 **/
    private int skillId;
    /** 释放战斗单元标识 **/
    private long caster;
    /** 下次执行的技能阶段 **/
    private SkillStage nextStage = SkillStage.START;
    /** 目标 **/
    private SelectorResult target;
    /** 创建技能回合数 **/
    private final long createRound;
    /** 下一次执行技能效果的回合 **/
    private long nextRound;
    /** 技能失效的回合 **/
    private long expireRound;
    /** 选择目标相关参数 **/
    private SelectParam param;
    /** 技能上下文 **/
    private final AbstractDamageAlterContext context;

    public BattleSkill(int skillId, long caster, SelectorResult target, int durationRound, SelectParam param, AbstractDamageAlterContext context) {
        this.skillId = skillId;
        this.caster = caster;
        this.target = target;
        this.context = context;
        this.param = param;
        this.createRound = context.getScene().getSceneRound();
        this.nextRound = createRound;
        this.expireRound = createRound + durationRound;

    }

    public void updateSkillStage(SkillStage nextStage) {
        this.nextStage = nextStage;
    }


    public boolean isExpire(long curRound) {
        return expireRound != 0 && curRound > expireRound;
    }

    @Override
    public long getOwner() {
        return caster;
    }
}

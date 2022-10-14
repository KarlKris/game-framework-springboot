package com.li.battle.skill;

import com.li.battle.core.IOwner;
import com.li.battle.core.scene.BattleScene;
import com.li.battle.core.unit.*;
import com.li.battle.selector.*;
import lombok.Getter;

import java.util.*;

/**
 * 场景中存在的技能上下文
 * @author li-yuanwen
 * @date 2021/10/20
 */
@Getter
public class BattleSkill implements IOwner {

    /** 技能配置 **/
    private final int skillId;
    /** 释放战斗单元标识 **/
    private final long caster;
    /** 下次执行的技能阶段 **/
    private SkillStage nextStage = SkillStage.START;
    /** 目标 **/
    private final SelectorResult target;
    /** 创建技能回合数 **/
    private final long createRound;
    /** 引导技能开始回合数 **/
    private long channelStartRound;
    /** 下一次执行技能效果的回合 **/
    private long nextRound;
    /** 技能失效的回合 **/
    private final long expireRound;
    /** 选择目标相关参数 **/
    private final SelectParam param;
    /** 关联的战斗场景 **/
    private final BattleScene scene;

    public BattleSkill(int skillId, long caster, SelectorResult target, int durationRound, SelectParam param,  BattleScene scene) {
        this.skillId = skillId;
        this.caster = caster;
        this.target = target;
        this.scene = scene;
        this.param = param;
        this.createRound = scene.getSceneRound();
        this.nextRound = createRound;
        this.expireRound = createRound + durationRound + 1;

    }

    public void updateSkillStage(SkillStage nextStage) {
        this.nextStage = nextStage;
    }

    public void addNextRound(long addRound) {
        this.nextRound += addRound;
        if (this.expireRound > 0) {
            this.nextRound = Math.min(this.nextRound, this.expireRound);
        }
    }

    public void initChannelStartRound() {
        this.channelStartRound = scene.getSceneRound();
    }

    public boolean isExpire(long curRound) {
        return expireRound != 0 && curRound >= expireRound;
    }


    public List<IPosition> getFinalTargets() {
        List<IPosition> results = target.getResults();
        // todo 应该根据技能来判断需不需要过滤自己

        Iterator<IPosition> iterator = results.iterator();
        while (iterator.hasNext()) {
            IPosition next = iterator.next();
            if (next instanceof FightUnit) {
                FightUnit unit = (FightUnit) next;
                // 过滤目标中包含自己
                if (unit.getId() == caster) {
                    iterator.remove();
                }
            }
        }
        return results;
    }

    @Override
    public long getOwner() {
        return caster;
    }
}

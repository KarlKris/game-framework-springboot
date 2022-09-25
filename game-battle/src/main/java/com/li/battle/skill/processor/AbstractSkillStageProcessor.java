package com.li.battle.skill.processor;

import cn.hutool.core.util.ArrayUtil;
import com.li.battle.core.scene.BattleScene;
import com.li.battle.core.unit.FightUnit;
import com.li.battle.effect.*;
import com.li.battle.effect.domain.EffectParam;
import com.li.battle.effect.source.SkillEffectSource;
import com.li.battle.skill.BattleSkill;

/**
 * 技能阶段效果执行器基类
 * @author li-yuanwen
 * @date 2022/5/30
 */
public abstract class AbstractSkillStageProcessor<T> implements SkillStageProcessor<T> {


    /**
     * 判断效果是否可执行
     * @param effectParams 效果
     * @return true
     */
    protected boolean isExecutable(EffectParam[] effectParams) {
        return ArrayUtil.isNotEmpty(effectParams);
    }

    /**
     * 执行效果
     * @param skill 技能
     * @param effectParams 效果
     */
    protected void process0(BattleSkill skill, EffectParam[] effectParams) {
        SkillEffectSource source = new SkillEffectSource(skill);
        EffectExecutor effectExecutor = skill.getScene().battleSceneHelper().effectExecutor();
        for (EffectParam effectParam : effectParams) {
            effectExecutor.execute(source, effectParam);
        }
    }


    /**
     * 让技能进CD
     * @param skill 技能
     */
    protected void makeSkillStartCoolDown(BattleSkill skill) {
        // 技能进CD
        BattleScene scene = skill.getScene();
        FightUnit fightUnit = scene.getFightUnit(skill.getCaster());
        fightUnit.coolDownSkill(skill.getSkillId());
    }


}

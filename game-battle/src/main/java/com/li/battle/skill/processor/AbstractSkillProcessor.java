package com.li.battle.skill.processor;

import cn.hutool.core.util.ArrayUtil;
import com.li.battle.buff.core.Buff;
import com.li.battle.core.scene.BattleScene;
import com.li.battle.core.unit.FightUnit;
import com.li.battle.effect.Effect;
import com.li.battle.skill.BattleSkill;

/**
 * 技能阶段效果执行器基类
 * @author li-yuanwen
 * @date 2022/5/30
 */
public abstract class AbstractSkillProcessor<T> implements SkillProcessor<T> {


    /**
     * 判断效果是否可执行
     * @param effects 效果
     * @return true
     */
    protected boolean isExecutable(Effect<Buff>[] effects) {
        return ArrayUtil.isNotEmpty(effects);
    }

    /**
     * 执行效果
     * @param skill 技能
     * @param effects 效果
     */
    protected void process0(BattleSkill skill, Effect<Buff>[] effects) {
        for (Effect<Buff> effect : effects) {
            effect.onAction(skill);
        }
    }


    /**
     * 让技能进CD
     * @param skill 技能
     */
    protected void makeSkillStartCoolDown(BattleSkill skill) {
        // 技能进CD
        BattleScene scene = skill.getContext().getScene();
        FightUnit fightUnit = scene.getFightUnit(skill.getCaster());
        fightUnit.coolDownSkill(skill.getSkillId());
    }


}

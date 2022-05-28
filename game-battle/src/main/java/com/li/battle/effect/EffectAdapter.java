package com.li.battle.effect;

import com.li.battle.buff.core.Buff;
import com.li.battle.core.unit.FightUnit;
import com.li.battle.skill.BattleSkill;
import lombok.extern.slf4j.Slf4j;

/**
 * 效果适配器
 * @author li-yuanwen
 * @date 2022/5/24
 */
@Slf4j
public class EffectAdapter<B extends Buff> implements Effect<B> {

    @Override
    public void onAction(FightUnit unit) {
        log.info("战斗单位:[{},{}]执行效果[{}]", unit.getClass().getSimpleName(), unit.getId(), this.getClass().getSimpleName());
    }

    @Override
    public void onAction(BattleSkill skill) {
        log.info("技能:[{}]执行效果[{}]", skill.getSkillId(), this.getClass().getSimpleName());
    }

    @Override
    public void onAction(B buff) {
        log.info("buff:[{}]执行效果[{}]", buff.getBuffId(), this.getClass().getSimpleName());
    }

    @Override
    public void onAction(FightUnit caster, FightUnit target) {
        log.info("战斗单位:[{},{}]向战斗单位:[{}]执行效果[{},{}]", caster.getClass().getSimpleName(), caster.getId()
                , target.getClass().getSimpleName(), target.getId()
                , this.getClass().getSimpleName());
    }
}

package com.li.battle.effect;

import com.li.battle.buff.core.Buff;
import com.li.battle.core.unit.FightUnit;
import com.li.battle.skill.BattleSkill;
import lombok.Getter;

/**
 * 添加子弹效果
 * @author li-yuanwen
 * @date 2022/6/6
 */
@Getter
public class ProjectileEffect extends EffectAdapter<Buff> {

    /** 子弹配置标识 **/
    private int projectileId;

    @Override
    public void onAction(BattleSkill skill) {
        FightUnit caster = skill.getContext().getScene().getFightUnit(skill.getSkillId());
        // todo
    }

}

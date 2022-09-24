package com.li.battle.effect.source;

import com.li.battle.core.Skill;
import com.li.battle.core.scene.BattleScene;
import com.li.battle.core.unit.*;

import java.util.*;

/**
 * 被动效果来源
 * @author li-yuanwen
 * @date 2022/9/22
 */
public class PassiveEffectSource extends AbstractEffectSource {

    /** 被动持有战斗单位 **/
    private final FightUnit unit;
    /** 被动技能 **/
    private final Skill skill;

    public PassiveEffectSource(FightUnit unit, Skill skill) {
        this.unit = unit;
        this.skill = skill;
    }

    @Override
    public FightUnit getCaster() {
        return unit;
    }

    @Override
    public List<IPosition> getTargets() {
        return Collections.singletonList(unit);
    }

    @Override
    public List<FightUnit> getTargetUnits() {
        return Collections.singletonList(unit);
    }

    @Override
    public BattleScene battleScene() {
        return unit.getScene();
    }

    @Override
    public int getSkillId() {
        return skill.getSkillId();
    }

    @Override
    public int getBuffId() {
        return 0;
    }

}

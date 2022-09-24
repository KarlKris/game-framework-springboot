package com.li.battle.effect.source;

import com.li.battle.core.scene.BattleScene;
import com.li.battle.core.unit.*;
import com.li.battle.skill.BattleSkill;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 技能效果来源
 * @author li-yuanwen
 * @date 2022/9/22
 */
public class SkillEffectSource extends AbstractEffectSource {

    /** 技能 **/
    private final BattleSkill battleSkill;

    public SkillEffectSource(BattleSkill battleSkill) {
        this.battleSkill = battleSkill;
    }

    @Override
    public FightUnit getCaster() {
        return battleSkill.getScene().getFightUnit(battleSkill.getCaster());
    }

    @Override
    public List<IPosition> getTargets() {
        return battleSkill.getFinalTargets();
    }

    @Override
    public List<FightUnit> getTargetUnits() {
        return battleSkill.getFinalTargets().stream()
                .filter(p -> p instanceof FightUnit)
                .map(p -> (FightUnit) p)
                .collect(Collectors.toList());
    }

    @Override
    public BattleScene battleScene() {
        return battleSkill.getScene();
    }

    @Override
    public int getSkillId() {
        return battleSkill.getSkillId();
    }

    @Override
    public int getBuffId() {
        return 0;
    }

}

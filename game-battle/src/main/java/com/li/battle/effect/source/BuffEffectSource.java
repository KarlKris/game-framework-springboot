package com.li.battle.effect.source;

import com.li.battle.buff.core.Buff;
import com.li.battle.core.scene.BattleScene;
import com.li.battle.core.unit.*;

import java.util.*;

/**
 * buff效果
 * @author li-yuanwen
 * @date 2022/9/22
 */
public class BuffEffectSource extends AbstractEffectSource {

    /** buff **/
    private final Buff buff;

    public BuffEffectSource(Buff buff) {
        this.buff = buff;
    }

    @Override
    public FightUnit getCaster() {
        return buff.battleScene().getFightUnit(buff.getCaster());
    }

    @Override
    public List<IPosition> getTargets() {
        return Collections.singletonList(getParentFightUnit());
    }

    @Override
    public List<FightUnit> getTargetUnits() {
        return Collections.singletonList(getParentFightUnit());
    }


    @Override
    public int getSkillId() {
        return buff.getSkillId();
    }

    @Override
    public BattleScene battleScene() {
        return buff.battleScene();
    }

    @Override
    public int getBuffId() {
        return buff.getBuffId();
    }

    private FightUnit getParentFightUnit() {
        return buff.battleScene().getFightUnit(buff.getParent());
    }

}

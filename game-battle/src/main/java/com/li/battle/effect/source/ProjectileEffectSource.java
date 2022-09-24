package com.li.battle.effect.source;

import com.li.battle.core.scene.BattleScene;
import com.li.battle.core.unit.*;
import com.li.battle.projectile.Projectile;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 子弹效果
 * @author li-yuanwen
 * @date 2022/9/22
 */
public class ProjectileEffectSource extends AbstractEffectSource {

    /** 子弹 **/
    private final Projectile projectile;
    /** 碰撞单位 **/
    private final List<FightUnit> hitUnits;

    public ProjectileEffectSource(Projectile projectile, List<FightUnit> hitUnits) {
        this.projectile = projectile;
        this.hitUnits = hitUnits;
    }


    @Override
    public FightUnit getCaster() {
        return projectile.battleScene().getFightUnit(projectile.getOwner());
    }

    @Override
    public List<IPosition> getTargets() {
        return hitUnits.stream().map(unit -> (IPosition) unit).collect(Collectors.toList());
    }

    @Override
    public List<FightUnit> getTargetUnits() {
        return hitUnits;
    }

    @Override
    public BattleScene battleScene() {
        return projectile.battleScene();
    }

    @Override
    public int getSkillId() {
        return projectile.getSkillId();
    }

    @Override
    public int getBuffId() {
        return 0;
    }
}

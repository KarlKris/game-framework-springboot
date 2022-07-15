package com.li.battle.projectile;

import com.li.battle.collision.Rectangle;
import com.li.battle.core.scene.BattleScene;
import com.li.battle.core.unit.FightUnit;
import com.li.battle.core.unit.IPosition;
import com.li.battle.resource.ProjectileConfig;
import com.li.battle.skill.BattleSkill;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 追踪子弹创建器
 * @author li-yuanwen
 * @date 2022/6/2
 */
@Component
public class TrackingProjectileCreator implements ProjectileCreator {

    @Override
    public ProjectileType getType() {
        return ProjectileType.TRACKING_PROJECTILE;
    }

    @Override
    public Projectile newInstance(BattleSkill skill, ProjectileConfig config) {
        BattleScene scene = skill.getContext().getScene();

        FightUnit caster = scene.getFightUnit(skill.getCaster());
        // todo 玩家已死亡则放弃创建子弹

        Vector2D position = caster.getPosition();
        List<IPosition> results = skill.getTarget().getResults();
        if (results.isEmpty()) {
            return null;
        }

        FightUnit targetUnit = results.stream()
                .filter(p -> p instanceof FightUnit)
                .map(p -> (FightUnit) p)
                .findFirst().orElse(null);

        if (targetUnit == null) {
            return null;
        }

        // 实际终点
        Vector2D end = targetUnit.getPosition().subtract(position).normalize()
                .scalarMultiply(config.getLength())
                .add(position);
        // 碰撞矩形
        Rectangle rectangle = new Rectangle(position, end, config.getWidth() >> 1);

        return new TrackingProjectile(scene, config, caster.getId(), skill.getSkillId(), position, targetUnit.getId(), rectangle);
    }
}

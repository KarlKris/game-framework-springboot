package com.li.battle.projectile;

import com.li.battle.collision.Rectangle;
import com.li.battle.core.scene.BattleScene;
import com.li.battle.core.unit.*;
import com.li.battle.effect.source.EffectSource;
import com.li.battle.resource.ProjectileConfig;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 线性子弹创建器
 * @author li-yuanwen
 * @date 2022/6/2
 */
@Component
public class LinearProjectileCreator implements ProjectileCreator {

    @Override
    public ProjectileType getType() {
        return ProjectileType.LINEAR_PROJECTILE;
    }

    @Override
    public Projectile newInstance(EffectSource source, ProjectileConfig config) {
        BattleScene scene = source.battleScene();

        FightUnit caster = source.getCaster();
        // todo 玩家已死亡则放弃创建子弹

        List<IPosition> results = source.getTargets();
        if (results.isEmpty()) {
            return null;
        }

        Vector2D targetPosition = results.get(0).getPosition();

        Vector2D position = caster.getPosition();

        // 实际终点
        Vector2D end = targetPosition.subtract(position).normalize()
                .scalarMultiply(config.getLength())
                .add(position);

        // 碰撞矩形
        Rectangle rectangle = new Rectangle(position, end, config.getWidth() >> 1);

        return new LinearProjectile(scene, config, caster.getId(), source.getSkillId(), source.getBuffId(), position, rectangle);
    }
}

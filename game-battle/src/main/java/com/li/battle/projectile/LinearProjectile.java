package com.li.battle.projectile;

import com.li.battle.collision.Rectangle;
import com.li.battle.core.scene.BattleScene;
import com.li.battle.core.unit.FightUnit;
import com.li.battle.resource.ProjectileConfig;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

import java.util.List;

/**
 * 线性子弹
 * 子弹为等腰梯形检测盒子，无需目标，创建出来后沿着特定方向飞行一段距离。
 * 飞行途中进行相交测试(SweepTest)，对检测范围内的目标调用技能执行OnProjectileHit触发效果。
 * 到达目的地后触发OnProjectileHit(此时hitTarget为空）并自我销毁。
 * @author li-yuanwen
 */
public class LinearProjectile extends AbstractProjectile {

    /** 子弹初始位置 **/
    private final Vector2D start;

    public LinearProjectile(BattleScene scene, ProjectileConfig config, long owner, int skillId, int buffId
            , Vector2D position, Rectangle rectangle) {
        super(scene, config, owner, skillId, buffId, position, rectangle);
        this.start = position;
    }


    @Override
    protected void afterExecHitEffect() {
        // 是否可拦截
        boolean intercept = config.isIntercept();
        if (!intercept) {
            return;
        }
        // 子弹可拦截,直接销毁
        destroy();
    }

    @Override
    public boolean checkFinish() {
        return destroy || Vector2D.distance(start, position) > config.getRange();
    }

    @Override
    protected List<FightUnit> filter0(List<FightUnit> units) {
        return units;
    }

    @Override
    protected Vector2D getTargetPosition() {
        // 按飞行方向飞行speed
        // 速度 => 方向向量*飞行距离
        Vector2D velocity = rectangle.getDirectionVector2D().scalarMultiply(config.getSpeed());
        return position.add(velocity);
    }
}

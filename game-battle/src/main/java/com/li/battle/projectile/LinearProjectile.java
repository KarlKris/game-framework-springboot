package com.li.battle.projectile;

import com.li.battle.core.scene.BattleScene;
import com.li.battle.util.Rectangle;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

/**
 * todo 后续实现方法
 * 线性子弹
 * 子弹为等腰梯形检测盒子，无需目标，创建出来后沿着特定方向飞行一段距离。
 * 飞行途中进行相交测试(SweepTest)，对检测范围内的目标调用技能执行OnProjectileHit触发效果。
 * 到达目的地后触发OnProjectileHit(此时hitTarget为空）并自我销毁。
 * @author li-yuanwen
 */
public abstract class LinearProjectile extends AbstractProjectile {




    public LinearProjectile(BattleScene scene, int projectileId, long owner, int skillId
            , Vector2D position, int speed, Rectangle rectangle) {
        super(scene, projectileId, owner, skillId, position, speed, rectangle);
    }

}

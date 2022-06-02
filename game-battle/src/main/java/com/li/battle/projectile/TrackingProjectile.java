package com.li.battle.projectile;

import com.li.battle.core.Attribute;
import com.li.battle.core.scene.BattleScene;
import com.li.battle.core.unit.FightUnit;
import com.li.battle.util.Rectangle;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * todo 后续实现方法
 * 追踪子弹
 * 子弹具有一个目标Target，它创建出来后以直线速度飞向指定目标。
 * 命中目标或者到达目标位置后调用技能执行OnProjectileHit(projHandle, hitTarget, hitPosition)触发效果并自我销毁，
 * hitTarget可为空（目标有可能在飞行途中死亡或消失）。
 * @author li-yuanwen
 */
public abstract class TrackingProjectile extends AbstractProjectile {

    /** 子弹追踪的目标 **/
    private final long target;

    public TrackingProjectile(BattleScene scene, int projectileId, long owner, int skillId
            , Vector2D position, int speed, long target, Rectangle rectangle) {
        super(scene, projectileId, owner, skillId, position, speed, rectangle);
        this.target = target;
    }

    @Override
    protected List<FightUnit> tryHit0(List<FightUnit> units) {
        // 是否可拦截
        boolean intercept = scene.battleSceneHelper().configHelper().getProjectileConfigById(projectileId).isIntercept();

        Optional<FightUnit> optional = units.stream().filter(unit -> unit.getId() == target).findFirst();
        // todo 碰撞检测
        return optional.map(Collections::singletonList).orElse(Collections.emptyList());
    }

    @Override
    protected Vector2D getTargetPosition() {
        return scene.getFightUnit(target).getPosition();
    }

    @Override
    public boolean checkFinish() {
        // 目标已死亡
        return scene.getFightUnit(target).getAttributeValue(Attribute.HP) <= 0;
    }
}

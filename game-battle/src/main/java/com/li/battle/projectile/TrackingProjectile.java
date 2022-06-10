package com.li.battle.projectile;

import com.li.battle.core.Attribute;
import com.li.battle.core.scene.BattleScene;
import com.li.battle.core.unit.FightUnit;
import com.li.battle.util.Rectangle;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * 追踪子弹
 * 子弹具有一个目标Target，它创建出来后以直线速度飞向指定目标。
 * 命中目标或者到达目标位置后调用技能触发效果并自我销毁，（目标有可能在飞行途中死亡或消失）。
 * @author li-yuanwen
 */
public class TrackingProjectile extends AbstractProjectile {

    /** 子弹追踪的目标 **/
    private final long target;

    public TrackingProjectile(BattleScene scene, int projectileId, long owner, int skillId
            , Vector2D position, int speed, long target, Rectangle rectangle) {
        super(scene, projectileId, owner, skillId, position, speed, rectangle);
        this.target = target;
    }

    @Override
    protected List<FightUnit> filter0(List<FightUnit> units) {
        // 是否可拦截
        boolean intercept = scene.battleSceneHelper().configHelper().getProjectileConfigById(projectileId).isIntercept();
        if (!intercept) {
            Optional<FightUnit> optional = units.stream().filter(unit -> unit.getId() == target).findFirst();
            return optional.map(Collections::singletonList).orElse(Collections.emptyList());

        }

        // 可拦截,按照子弹距离排序
        units.sort(Comparator.comparingDouble(o -> Vector2D.distance(o.getPosition(), position)));
        return Collections.singletonList(units.get(0));

    }

    @Override
    protected void afterExecHitEffect() {
        // 结束子弹生命周期
        destroy();
    }

    @Override
    protected Vector2D getTargetPosition() {
        return scene.getFightUnit(target).getPosition();
    }

    @Override
    public boolean checkFinish() {
        // 子弹已销毁或目标已死亡
        return destroy || scene.getFightUnit(target).getAttributeValue(Attribute.HP) <= 0;
    }

}

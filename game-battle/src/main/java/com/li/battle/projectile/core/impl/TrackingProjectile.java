package com.li.battle.projectile.core.impl;

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
    private long target;

    public TrackingProjectile(long owner, int skillId, int[] fromPosition, int speed, long target) {
        super(owner, skillId, fromPosition, speed);
        this.target = target;
    }

}

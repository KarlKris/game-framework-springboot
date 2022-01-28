package com.li.battle.projectile.core.impl;

/**
 * todo 后续实现方法
 * 线性子弹
 * 子弹为等腰梯形检测盒子，无需目标，创建出来后沿着特定方向飞行一段距离。
 * 飞行途中进行相交测试(SweepTest)，对检测范围内的目标调用技能执行OnProjectileHit触发效果。
 * 到达目的地后触发OnProjectileHit(此时hitTarget为空）并自我销毁。
 * @author li-yuanwen
 */
public abstract class LinearProjectile extends AbstractProjectile {

    // todo 成员属性：飞行方向,等腰梯形检测盒（起点宽度，终点宽度，两者距离）,子弹筛选目标信息

    /** 子弹飞行的方向 **/


    /** 等腰梯形检测盒 **/


    /** 子弹筛选的目标信息 **/


    public LinearProjectile(long owner, int skillId, int[] fromPosition, int speed) {
        super(owner, skillId, fromPosition, speed);
    }

}

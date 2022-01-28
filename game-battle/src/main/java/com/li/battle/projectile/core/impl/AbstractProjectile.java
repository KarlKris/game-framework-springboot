package com.li.battle.projectile.core.impl;

import com.li.battle.projectile.core.Projectile;

/**
 * 抽象子弹基类
 * @author li-yuanwen
 */
public abstract class AbstractProjectile implements Projectile {

    /** 子弹创建者 **/
    protected long owner;

    /** 子弹关联的技能 **/
    protected int skillId;

    /** 子弹当前位置 **/
    protected int[] position;

    /** 子弹的飞行速率 **/
    protected int speed;

    AbstractProjectile(long owner, int skillId, int[] fromPosition, int speed) {
        this.owner = owner;
        this.skillId = skillId;
        this.position = fromPosition;
        this.speed = speed;
    }

}

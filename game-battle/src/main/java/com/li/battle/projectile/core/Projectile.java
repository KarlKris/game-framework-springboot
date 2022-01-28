package com.li.battle.projectile.core;

/**
 * 子弹抽象接口--子弹本身是没有任何特殊逻辑的，它只有位置更新，检查命中目标和是否结束并销毁这几个简单的功能
 * @author li-yuanwen
 */
public interface Projectile {


    /** 位置更新 **/
    void updatePosition();

    /**
     * 检查命中目标
     * @return true 命中目标
     */
    boolean checkProjectileHit();

    /**
     * 检查是否结束
     * @return true 结束
     */
    boolean checkFinish();


}

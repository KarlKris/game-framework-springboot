package com.li.battle.buff.core.impl;

/**
 * 继承于BuffModifier,代表此类Buff提供修改玩家运动效果的功能
 * @author li-yuanwen
 */
public abstract class BuffMotionModifier extends BuffModifier {

    /** 修改运动 **/
    abstract void modifyMotion();

}

package com.li.battle.buff.core.impl;

/**
 * 继承于AbstractBuff,代表这个Buff是一个修改器,可以用来修改当前目标的各种属性，状态等等
 * @author li-yuanwen
 */
public abstract class BuffModifier extends AbstractBuff {

    /** 修改状态 **/
    abstract void modifyState();

    /** 修改属性 **/
    abstract void modifyAttribute();
}

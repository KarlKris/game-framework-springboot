package com.li.battle.buff.core.impl;

import com.li.battle.buff.core.Buff;
import com.li.battle.core.context.BuffCreateContext;

/**
 * 所有buff的基类，包含各类成员函数和基本接口
 * @author li-yuanwen
 */
public abstract class AbstractBuff implements Buff {

    /** buff唯一标识 **/
    protected int buffId;

    /** buff施加者 **/
    protected long caster;

    /** 当前挂载的目标 **/
    protected long parent;

    /** buff由哪个技能创建 **/
    protected int skillAbilityId;

    /** buff层数 **/
    protected int layer;

    /** buff等级 **/
    protected int level;

    /** 下一次触发间隔效果的回合数 **/
    protected long nextRound;

    /** buff失效回合数=创建时回合数 + (buff时长(毫秒) / 回合执行间隔时长(毫秒)) **/
    protected long expireRound;

    /** buff创建时的一些相关上下文数据 **/
    protected BuffCreateContext context;

    @Override
    public void expire() {
        expireRound = -1;
    }

    /** buff是否失效 **/
    @Override
    public boolean expire(long curRound) {
        return curRound >= expireRound;
    }

    @Override
    public long getNextRound() {
        return nextRound;
    }
}

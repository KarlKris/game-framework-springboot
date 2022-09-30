package com.li.battle.core;

/**
 * 单元状态
 * @author li-yuanwen
 * @date 2021/10/16
 */
public enum UnitState {

    /** 正常 **/
    NORMAL(true, true, true),

    /** 寻路中 **/
    MOVING(true, true, true),

    /** 寻路等待状态 **/
    MOVING_WAIT(false, false, true),

    /** 前摇 **/
    FRONT(false, false, true),

    /** 后摇 **/
    BACK(false, false, true),

    // -------- 异常类状态 -------------------

    /** 眩晕-目标不再响应任何操控 **/
    STUN(false, false, true),

    /** 缠绕-又称定身——目标不响应移动请求，但是可以执行某些操作，如施放某些技能 **/
    ROOT(false, true, true),

    /** 沉默-目标禁止施放技能 **/
    SILENCE(true, false, true),

    /** 无敌-几乎不受到所有的伤害和效果影响 **/
    INVINCIBLE(true, true, true),

    /** 隐身-不可被其他人看见 **/
    INVISIBLE(true, true, false),

    /** 击飞-类眩晕，不再响应任何操作 **/
    KNOCK(false, false, true),

    ;

    /** 能否响应移动请求 **/
    private final boolean move;
    /** 能否释放技能 **/
    private final boolean freed;
    /** 能否被他人看见 **/
    private final boolean see;

    UnitState(boolean move, boolean freed, boolean see) {
        this.move = move;
        this.freed = freed;
        this.see = see;
    }

    public boolean canMove() {
        return move;
    }

    public boolean canFreed() {
        return freed;
    }

    public boolean canSee() {
        return see;
    }
}

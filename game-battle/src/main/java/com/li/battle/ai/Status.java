package com.li.battle.ai;

/**
 * 状态,每个行为在执行后都会传回一个状态
 * @author li-yuanwen
 * @date 2022/1/25
 */
public enum Status {

    /**
     * 完成状态
     **/
    SUCCESS,

    /**
     * 正在执行
     **/
    RUNNING,

    /**
     * 失败
     **/
    FAILURE,

    /**
     * 非法状态
     **/
    INVALID,

    ;
}

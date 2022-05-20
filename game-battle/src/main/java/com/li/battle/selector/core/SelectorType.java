package com.li.battle.selector.core;

/**
 * 选择器类型
 * @author li-yuanwen
 * @date 2022/5/17
 */
public enum SelectorType {

    // 选择器大概就以下3大类型,集中选定目标和选定地点均可扩展

    /** 不需要目标 **/
    NONE,

    /** 选定目标 **/
    SELECT_TARGET,

    /** 选定地点 **/
    SELECT_POSITION,

    ;


}

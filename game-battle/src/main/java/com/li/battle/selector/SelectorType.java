package com.li.battle.selector;

/**
 * 选择器类型
 * @author li-yuanwen
 * @date 2022/5/17
 */
public enum SelectorType {

    // 选择器大概就以下4大类型
    // 1. 不需要目标（如群疗）
    // 2. 选定目标 （单体指向性技能）
    // 3. 选定地点 （常用于AOE技能,踩地板技能,位移）

    // 1
    /** 己方阵营全部战斗单位 **/
    SELF_CAMP,

    /** 敌方阵营全部战斗单位 **/
    ENEMY_CAMP,

    /** 自己 **/
    SELF,

    // 2
    /** 单个目标 **/
    SINGLE_TARGET,

    // 3
    /** 矩形内玩家 **/
    RECTANGLE,

    /** 扇形内玩家 **/
    SECTOR,

    /** 圆形内玩家 **/
    ROUND,

    /** 矩形坐标 **/
    RECTANGLE_COORDINATE,


    ;


}

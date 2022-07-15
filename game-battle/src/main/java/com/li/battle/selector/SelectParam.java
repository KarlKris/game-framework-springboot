package com.li.battle.selector;

import lombok.Getter;

/**
 * 选择目标相关参数
 * @author li-yuanwen
 * @date 2022/5/31
 */
@Getter
public class SelectParam {
    /** 空参数 **/
    public static final SelectParam EMPTY = new SelectParam();


    // ------------------- 用于选定目标型技能 ----------------------------

    /** 目标单位标识 **/
    private long target;


    // ------------------- 用于选定地点型技能 ----------------------------

    /** 技能释放目标中心点 **/
    private double x;
    private double y;

    /** 技能释放终点 **/
    private double endX;
    private double endY;

    // ------------------- 用于选定方向型技能 ----------------------------


    /** 技能释放目标点,单位当前位置面向目标点即为方向 **/
    private double directionX;
    private double directionY;

}

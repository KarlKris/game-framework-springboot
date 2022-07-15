package com.li.battle.collision;

import lombok.Getter;

/**
 * 垂直于坐标轴的矩形表述
 * @author li-yuanwen
 * @date 2022/5/31
 */
@Getter
public class Rectangle2D {

    /** 左下角坐标 **/
    private final double x;
    private final double y;

    /** 长宽 **/
    private final double width;
    private final double height;


    public Rectangle2D(double x, double y, double width, double height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
}

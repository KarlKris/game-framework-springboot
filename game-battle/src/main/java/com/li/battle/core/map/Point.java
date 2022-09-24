package com.li.battle.core.map;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 坐标点
 * @author li-yuanwen
 * @date 2022/6/7
 */
@Getter
@NoArgsConstructor
public class Point {

    /** 左下方横坐标 **/
    private int x;
    /** 左下方纵坐标 **/
    private int y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }
}

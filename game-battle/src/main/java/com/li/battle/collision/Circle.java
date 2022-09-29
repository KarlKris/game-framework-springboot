package com.li.battle.collision;

import lombok.Getter;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

/**
 * 圆
 * @author li-yuanwen
 * @date 2022/9/27
 */
@Getter
public class Circle implements ICircle {

    /** 圆心 **/
    private final Vector2D center;
    /** 半径 **/
    private final double radius;

    public Circle(Vector2D center, double radius) {
        this.center = center;
        this.radius = radius;
    }

    @Override
    public Vector2D getCentre() {
        return center;
    }
}

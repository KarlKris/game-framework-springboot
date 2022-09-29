package com.li.battle.collision;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

import java.util.*;

/**
 * 圆
 * @author li-yuanwen
 * @date 2022/9/29
 */
public interface ICircle extends Shape {


    /**
     * 获取圆心坐标
     * @return 圆心坐标
     */
    Vector2D getCentre();


    /**
     * 获取半径
     * @return 半径
     */
    double getRadius();

    @Override
    default List<Vector2D> getAllPoints() {
        List<Vector2D> points = new LinkedList<>();
        // 上下左右 四个点
        points.add(new Vector2D(getCentre().getX(), getTop()));
        points.add(new Vector2D(getCentre().getX(), getBottom()));
        points.add(new Vector2D(getLeft(), getCentre().getY()));
        points.add(new Vector2D(getRight(), getCentre().getY()));

        return points;
    }

    @Override
    default List<Vector2D> getAllProtectionAxis(CollisionDetector detector) {
        // 圆的投影轴只需要以圆心与多边形顶点中最近的一点的连线
        double minLength = Double.MAX_VALUE;
        Vector2D nearestPoint = null;
        for (Vector2D point : detector.getAllPoints()) {
            double distance = Vector2D.distance(getCentre(), point);
            if (distance < minLength) {
                minLength = distance;
                nearestPoint = point.subtract(getCentre());
            }
        }

        return nearestPoint == null ? Collections.emptyList() : Collections.singletonList(nearestPoint.normalize());
    }


    @Override
    default double getLeft() {
        return getCentre().getX() - getRadius();
    }

    @Override
    default double getRight() {
        return getCentre().getX() + getRadius();
    }

    @Override
    default double getTop() {
        return getCentre().getY() + getRadius();
    }

    @Override
    default double getBottom() {
        return getCentre().getY() - getRadius();
    }
}

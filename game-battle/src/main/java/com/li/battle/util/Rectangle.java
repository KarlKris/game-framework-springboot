package com.li.battle.util;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.apache.commons.math3.util.FastMath;

/**
 * 带有旋转角度的矩形表述
 * @author li-yuanwen
 * @date 2022/6/1
 */
public class Rectangle implements Shape {

    /** 矩形中点起点 **/
    private final Vector2D start;
    /** 矩形中点终点 **/
    private final Vector2D end;
    /** 矩形宽度(被中点平分) **/
    private final double halfWidth;
    /** 与横坐标角度余弦值 **/
    private final double cos;
    /** 矩形角度是横坐标上面还是下面 **/
    private final boolean up;
    /** 矩形角度是纵坐标左边还是右边 即角度是否大于90° **/
    private final boolean left;

    public Rectangle(Vector2D start, Vector2D end, double halfWidth) {
        this.start = start;
        this.end = end;
        this.halfWidth = halfWidth;

        this.cos = new Vector2D(end.getX(), start.getY()).subtract(start).getNorm() / end.subtract(start).getNorm();
        this.up = end.getY() > start.getY();
        this.left = end.getX() < start.getX();
    }

    public double getHalfWidth() {
        return halfWidth;
    }

    @Override
    public double getLeft() {
        double subY = cos * halfWidth;
        double subX = FastMath.sqrt(halfWidth * halfWidth - subY * subY);
        if (left) {
            return end.getX() - subX;
        } else {
            return end.getX() - subX;
        }
    }

    @Override
    public double getRight() {
        double subY = cos * halfWidth;
        double addX = FastMath.sqrt(halfWidth * halfWidth - subY * subY);
        if (left) {
            return start.getX() + addX;
        } else {
            return end.getX() + addX;
        }
    }

    @Override
    public double getTop() {
        double addY = cos * halfWidth;
        if (up) {
            return end.getY() + addY;
        } else {
            return start.getY() + addY;
        }
    }

    @Override
    public double getBottom() {
        double subY = cos * halfWidth;
        if (up) {
            // 角度偏上用起点坐标算最低值
            return start.getY() - subY;
        } else {
            return end.getY() - subY;
        }
    }

    public static void main(String[] args) {
        Vector2D start = new Vector2D(0, 0);
        Vector2D end = new Vector2D(-1, 1);
        Vector2D a = new Vector2D(-1, 0);

        double cos = (a.subtract(start).getNorm()) / end.subtract(start).getNorm();
        System.out.println(cos);

        Vector2D v = new Vector2D(1, 0);
        double angle = Vector2D.angle(end.subtract(start), v);
        System.out.println(Math.cos(angle));
        System.out.println(Math.toDegrees(angle));

    }
}

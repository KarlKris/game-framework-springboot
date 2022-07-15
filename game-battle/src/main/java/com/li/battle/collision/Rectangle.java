package com.li.battle.collision;

import com.li.battle.util.BattleMathUtil;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.apache.commons.math3.util.FastMath;

import java.util.LinkedList;
import java.util.List;

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
    /** 矩形角度是横坐标上面还是下面 **/
    private final boolean up;
    /** 矩形角度是纵坐标左边还是右边 即角度是否大于90° **/
    private final boolean left;
    /** 矩形start或end距离延申顶点的x轴长度 **/
    private final double xLength;
    /** 矩形start或end距离延申顶点的y轴长度 **/
    private final double yLength;

    public Rectangle(Vector2D start, Vector2D end, double halfWidth) {
        this.start = start;
        this.end = end;
        this.halfWidth = halfWidth;

        double cos = new Vector2D(end.getX(), start.getY()).subtract(start).getNorm() / end.subtract(start).getNorm();
        this.up = end.getY() > start.getY();
        this.left = end.getX() < start.getX();
        this.yLength = cos * halfWidth;
        this.xLength = FastMath.sqrt(halfWidth * halfWidth - yLength * yLength);
    }

    public double getHalfWidth() {
        return halfWidth;
    }

    public Vector2D getStart() {
        return start;
    }

    public Vector2D getEnd() {
        return end;
    }

    @Override
    public double getLeft() {
        if (left) {
            return end.getX() - xLength;
        } else {
            return start.getX() + xLength;
        }
    }

    private Vector2D getLeftPoint() {
        double x = getLeft();
        double y = 0;
        if (left) {
            y = end.getY() - yLength;
        } else {
            y = start.getY() + yLength;
        }
        return new Vector2D(x, y);
    }

    @Override
    public double getRight() {
        if (left) {
            return start.getX() + xLength;
        } else {
            return end.getX() + xLength;
        }
    }

    private Vector2D getRightPoint() {
        double x = getRight();
        double y = 0;
        if (left) {
            y = start.getY() + yLength;
        } else {
            y = end.getY() + yLength;
        }
        return new Vector2D(x, y);
    }

    @Override
    public double getTop() {
        if (up) {
            return end.getY() + yLength;
        } else {
            return start.getY() + yLength;
        }
    }

    private Vector2D getTopPoint() {
        double y = getTop();
        double x = 0;
        if (up) {
            x = end.getX() - xLength;
        } else {
            x = start.getX() + xLength;
        }
        return new Vector2D(x, y);
    }

    @Override
    public double getBottom() {
        if (up) {
            // 角度偏上用起点坐标算最低值
            return start.getY() - yLength;
        } else {
            return end.getY() - yLength;
        }
    }

    private Vector2D getBottomPoint() {
        double y = getBottom();
        double x = 0;
        if (up) {
            x = start.getX() + xLength;
        } else {
            x = end.getX() - xLength;
        }
        return new Vector2D(x, y);
    }

    @Override
    public List<Vector2D> getAllProtectionAxis(CollisionDetector detector) {
        // 边向量 即 是投影向量
        List<Vector2D> list = new LinkedList<>();
        // 长边
        Vector2D one = end.subtract(start).normalize();
        list.add(one);
        // 宽边,矩形即求长边的法向量即可
        list.add(BattleMathUtil.normalVector(one));
        return list;
    }

    public Vector2D getDirectionVector2D() {
        return end.subtract(start).normalize();
    }

    @Override
    public List<Vector2D> getAllPoints() {
        // 矩形的四个顶点
        List<Vector2D> points = new LinkedList<>();
        points.add(getTopPoint());
        points.add(getBottomPoint());
        points.add(getLeftPoint());
        points.add(getRightPoint());
        return points;
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

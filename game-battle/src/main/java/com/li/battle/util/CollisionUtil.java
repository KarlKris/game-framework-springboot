package com.li.battle.util;

import com.li.battle.collision.*;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * 碰撞检测工具类
 * @author li-yuanwen
 * @date 2022/7/8
 */
public class CollisionUtil {

    /** 误差 **/
    static final double NUMERICAL_FAULT = 0.001;

    /**
     * 判断2个检测体是否发生碰撞
     * @param detector1 检测体1
     * @param detector2 检测体2
     * @return true 发生碰撞
     */
    public static boolean isCollision(CollisionDetector detector1, CollisionDetector detector2) {
        boolean d1Circle = detector1 instanceof ICircle;
        boolean d2Circle = detector2 instanceof ICircle;
        if (d1Circle && d2Circle) {
            return isCollisionBetweenCircle((ICircle) detector1, (ICircle) detector2);
        } else if (d1Circle) {
            return isCollisionWithCircle((ICircle) detector1, detector2);
        } else if (d2Circle) {
            return isCollisionWithCircle((ICircle) detector2, detector1);
        } else {
            // 获取检测体的所有投影轴
            // 通过判断任意两个 凸多边形 在任意角度下的投影是否均存在重叠，来判断是否发生碰撞
            // 若在某一角度光源下，两物体的投影存在间隙，则为不碰撞，否则为发生碰撞。
            List<Vector2D> allProtectionAxis = detector1.getAllProtectionAxis(detector2);
            for (Vector2D protectionAxis : allProtectionAxis) {
                // 求出检测体在投影轴上的长度,其中最值范围表投影
                Range detector1Range = calculateProjectedLength(detector1, protectionAxis);
                Range detector2Range = calculateProjectedLength(detector2, protectionAxis);

                // 检测2者的投影范围是否有重合
                if (!detector1Range.isCoincide(detector2Range)) {
                    return false;
                }
            }
            return true;
        }
    }

    /**
     * 计算物体在投影轴上的长度
     * @param detector 碰撞检测体
     * @param projectionAxis 投影轴
     * @return 物体在投影轴上的长度
     */
    public static Range calculateProjectedLength(CollisionDetector detector, Vector2D projectionAxis) {
        List<Vector2D> allPoints = detector.getAllPoints();
        if (CollectionUtils.isEmpty(allPoints)) {
            return NONE_RANGE;
        }

        boolean init = false;
        double min = 0;
        double max = 0;
        // 将各个顶点投影到投影轴上,求出min,max (向量点积的几何意义)
        for (Vector2D point : allPoints) {
            double length = point.dotProduct(projectionAxis);
            if (!init) {
                min = length;
                max = length;
                init = true;
            } else {
                min = Math.min(length, min);
                max = Math.max(length, max);
            }
        }

        return new Range(min, max);
    }


    /**
     * 检测圆与碰撞体之间是否发生碰撞
     * @param c1 单元1
     * @param c2 单位2
     * @return true 发生碰撞
     */
    public static boolean isCollisionWithCircle(ICircle c1, CollisionDetector c2) {
        // 单元都是圆,只需要比较圆心距
        for (Vector2D point : c2.getAllPoints()) {
            if (point.distance(c1.getCentre()) <= c1.getRadius()) {
                return true;
            }
        }
        return false;
    }

    /**
     * 检测2个圆之间是否发生碰撞
     * @param c1 单元1
     * @param c2 单位2
     * @return true 发生碰撞
     */
    public static boolean isCollisionBetweenCircle(ICircle c1, ICircle c2) {
        // 单元都是圆,只需要比较圆心距
        return Vector2D.distance(c1.getCentre(), c2.getCentre()) < (c1.getRadius() + c2.getRadius());
    }


    public static boolean isNotEqualWithDouble(double x, double y) {
        // 2个值超过0.001则视为不相等
        return Math.abs(x - y) > NUMERICAL_FAULT;
    }

    public static boolean isSimilarBetween(Vector2D v1, Vector2D v2) {
        if (v1.equals(v2)) {
            return true;
        }

        return v1.distance(v2) < NUMERICAL_FAULT;
    }



    private static final Range NONE_RANGE = new Range(0, 0);

    /**
     * 范围
     */
    private static class Range {

        /** 最小值 **/
        private final double min;
        /** 最大值 **/
        private final double max;

        Range(double min, double max) {
            this.min = min;
            this.max = max;
        }

        /** 两个Range是否有重合 **/
        boolean isCoincide(Range other) {
            return other.min <= this.max && this.min <= other.max;
        }

    }

}

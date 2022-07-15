package com.li.battle.util;

import com.li.battle.collision.CollisionDetector;
import com.li.battle.core.unit.Unit;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.springframework.util.CollectionUtils;

/**
 * 碰撞检测工具类
 * @author li-yuanwen
 * @date 2022/7/8
 */
public class CollisionUtil {


    /**
     * 判断2个检测体是否发生碰撞
     * @param detector1 检测体1
     * @param detector2 检测体2
     * @return true 发生碰撞
     */
    public static boolean isCollision(CollisionDetector detector1, CollisionDetector detector2) {
        // 获取检测体的所有投影轴
        for (Vector2D protectionAxis : detector1.getAllProtectionAxis(detector2)) {
            // 求出检测体在投影轴上的长度,其中最值范围表投影
            Range detector1Range = calculateProjectedLength(detector1, protectionAxis);
            Range detector2Range = calculateProjectedLength(detector2, protectionAxis);

            // 检测2者的投影范围是否有重合
            if (detector1Range.isCoincide(detector2Range)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 计算物体在投影轴上的长度
     * @param detector 碰撞检测体
     * @param projectionAxis 投影轴
     * @return 物体在投影轴上的长度
     */
    public static Range calculateProjectedLength(CollisionDetector detector, Vector2D projectionAxis) {
        if (CollectionUtils.isEmpty(detector.getAllPoints())) {
            return NONE_RANGE;
        }

        double min = Double.MAX_VALUE;
        double max = Double.MIN_VALUE;
        // 将各个顶点投影到投影轴上,求出min,max (向量点积的几何意义)
        for (Vector2D point : detector.getAllPoints()) {
            double length = projectionAxis.dotProduct(point);
            min = Math.min(length, min);
            max = Math.max(length, max);
        }

        return new Range(min, max);
    }


    /**
     * 检测2个Unit之间是否发生碰撞
     * @param unit1 单元1
     * @param unit2 单位2
     * @return true 发生碰撞
     */
    public static boolean isCollisionBetweenUnit(Unit unit1, Unit unit2) {
        // 单元都是圆,只需要比较圆心距
        return Vector2D.distance(unit1.getPosition(), unit2.getPosition()) < (unit1.getRadius() + unit2.getRadius());
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

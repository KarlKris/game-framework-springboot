package com.li.battle.util;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

/**
 * 战斗中涉及数学相关工具类
 * @author li-yuanwen
 * @date 2022/5/27
 */
public class BattleMathUtil {


    /**
     * 求向量的法向量
     * @param vector2D 向量
     * @return 向量的法向量
     */
    public static Vector2D normalVector(Vector2D vector2D) {
        // (x,y)坐标互换,且一个为相反数即可
        return new Vector2D(-vector2D.getY(), vector2D.getX()).normalize();
    }


    /**
     * 2D变换矩阵应用于单个Vector2D
     * @param matrix 二维矩阵
     * @param point 二维向量
     * @return Vector2D
     */
    public static Vector2D matrixTransformVector2D(double[][] matrix, Vector2D point) {
        double x = matrix[0][0] * point.getX() + matrix[1][0] * point.getY() + matrix[2][0];
        double y = matrix[0][1] * point.getX() + matrix[1][1] * point.getY() + matrix[2][1];

        return new Vector2D(x, y);
    }

    /**
     * 从二维向量创建旋转矩阵
     * @param v1 向量1
     * @param v2 向量2
     * @return 矩阵
     */
    public static double[][] rotate(Vector2D v1, Vector2D v2) {
        return new double[][] {
                {v1.getX(), v1.getY(), 0},
                {v2.getX(), v2.getY(), 0},
                {0, 0, 1}
        };
    }

}

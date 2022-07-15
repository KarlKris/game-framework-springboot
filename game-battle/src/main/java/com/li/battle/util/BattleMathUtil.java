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
        return new Vector2D(vector2D.getY(), - vector2D.getX()).normalize();
    }

}

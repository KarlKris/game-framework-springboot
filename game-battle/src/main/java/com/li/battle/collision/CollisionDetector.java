package com.li.battle.collision;

import com.li.battle.util.CollisionUtil;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

import java.util.List;

/**
 * 碰撞检测体(分离轴定理检测)
 * @author li-yuanwen
 * @date 2022/7/8
 */
public interface CollisionDetector {


    /**
     * 获取检测体各个顶点的坐标集
     * @return 检测体各个顶点的坐标集
     */
    List<Vector2D> getAllPoints();


    /**
     * 获取所有的投影轴(投影轴是单位向量)
     * @param detector 有些检测体的投影轴可能需要对应的检测体,例如圆
     * @return 所有的投影轴
     */
    List<Vector2D> getAllProtectionAxis(CollisionDetector detector);


    /**
     * 是否与detector发生碰撞
     * @param detector 碰撞检测体
     * @return true 发生碰撞
     */
    default boolean isCollision(CollisionDetector detector) {
        return CollisionUtil.isCollision(this, detector);
    }

}

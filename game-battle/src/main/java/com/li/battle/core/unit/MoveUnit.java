package com.li.battle.core.unit;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

import java.util.List;

/**
 * 移动单位接口
 * @author li-yuanwen
 * @date 2022/4/24
 */
public interface MoveUnit extends Unit {

    /**
     * 获取可达到的最大速度
     * @return 可达到的最大速度
     */
    int getMaxSpeed();

    /**
     * 获取当前速度矢量
     * @return 当前速度矢量
     */
    Vector2D getVelocity();

    /**
     * 获取当前速度 == getVelocity().getNorm()
     * @return 当前速度
     */
    double getSpeed();

    /**
     * 获取当前朝向
     * @return 当前朝向
     */
    Vector2D getHeading();

    /**
     * 获取wander圈上的点(局部)
     * @return wander圈上的点
     */
    Vector2D getLocalWander();

    /**
     * 更新wander圈上的点(局部)
     * @param localWander wander圈上的点
     */
    void updateLocalWander(Vector2D localWander);

    /**
     * 移动到指定地点
     * @param ways 路径
     */
    void moveTo(List<Vector2D> ways);

    /**
     * 执行单位移动
     */
    void moving();

}

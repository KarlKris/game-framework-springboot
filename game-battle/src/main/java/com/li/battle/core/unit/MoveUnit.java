package com.li.battle.core.unit;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

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

    // --------------------------

    /**
     * 更新单位坐标点
     * @param position 坐标点
     */
    void updatePosition(Vector2D position);

    /**
     * 设置移动目的坐标
     * @param position 坐标
     */
    void setMoveTargetPosition(Vector2D position);


    /**
     * 获取移动目的坐标
     * @return 坐标
     */
    Vector2D getMoveTargetPosition();

    /**
     * 更新单位的速度
     * @param velocity 速度
     */
    void updateVelocity(Vector2D velocity);

}

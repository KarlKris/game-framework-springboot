package com.li.battle.collision;

/**
 * 形状数据接口
 * @author li-yuanwen
 * @date 2022/6/1
 */
public interface Shape extends CollisionDetector {

    /**
     * 获取形状横坐标的的最左值
     * @return 横坐标的的最左值
     */
    double getLeft();

    /**
     * 获取形状横坐标的的最右值
     * @return 横坐标的的最右值
     */
    double getRight();

    /**
     * 获取形状纵坐标的的最上值
     * @return 纵坐标的的最上值
     */
    double getTop();

    /**
     * 获取形状纵坐标的的最底值
     * @return 纵坐标的的最底值
     */
    double getBottom();

}

package com.li.battle.core.scene.map;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

import java.util.List;

/**
 * 平面地图抽象接口
 * @author li-yuanwen
 * @date 2021/10/16
 */
public interface SceneMap {

    /**
     * 获取地图标识
     * @return 地图标识
     */
    int getMapId();


    /**
     * 获取地图格子大小
     * @return 地图格子大小
     */
    int getGridSize();


    /**
     * 获取地图横轴长度
     * @return 横轴长度
     */
    int getHorizontalLength();


    /**
     * 获取地图纵轴长度
     * @return 纵轴长度
     */
    int getVerticalLength();


    /**
     * 判断坐标为(x,y)所在的格子是否存在
     * @param x 横坐标
     * @param y 纵坐标
     * @return true 存在
     */
    boolean isExistGrid(double x, double y);


    /**
     * 获取坐标为(x,y)的所在的格子
     * @param x 横坐标
     * @param y 纵坐标
     * @return 格子
     */
    Grid getGrid(double x, double y);

    /**
     * 获取格子坐标点为(gridX, gridY)的格子
     * @param gridX 格子横坐标
     * @param gridY 格子纵坐标
     * @return 格子
     */
    Grid getGridByGridPoint(int gridX, int gridY);

    /**
     * 寻路,从(fromX, fromY)到(toX, toY)
     * @param fromX 起始点x坐标
     * @param fromY 起始点y坐标
     * @param toX 终点x坐标
     * @param toY 终点y坐标
     * @return 路径 or 空集(无路)
     */
    List<Vector2D> findWayByAStar(double fromX, double fromY, double toX, double toY);

}

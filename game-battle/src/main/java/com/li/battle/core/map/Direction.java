package com.li.battle.core.map;

import java.util.function.BiFunction;

/**
 * 方向枚举  8个方向
 * @author li-yuanwen
 * @date 2022/6/6
 */
public enum Direction {

    /** 北 **/
    NORTH((sceneMap, grid) -> {
        int x = grid.getX();
        int y = grid.getY() + grid.getSize();
        return sceneMap.getGridByGridPoint(x, y);
    }),
    /** 南 **/
    SOUTH((sceneMap, grid) -> {
        int x = grid.getX();
        int y = grid.getY() - grid.getSize();
        return sceneMap.getGridByGridPoint(x, y);
    }),
    /** 西 **/
    WEST((sceneMap, grid) -> {
        int x = grid.getX() - grid.getSize();
        int y = grid.getY();
        return sceneMap.getGridByGridPoint(x, y);
    }),
    /** 东 **/
    EAST((sceneMap, grid) -> {
        int x = grid.getX() + grid.getSize();
        int y = grid.getY();
        return sceneMap.getGridByGridPoint(x, y);
    }),
    /** 东北 **/
    NORTH_EAST((sceneMap, grid) -> {
        int x = grid.getX() + grid.getSize();
        int y = grid.getY() + grid.getSize();
        return sceneMap.getGridByGridPoint(x, y);
    }),
    /** 西北 **/
    NORTH_WEST((sceneMap, grid) -> {
        int x = grid.getX() - grid.getSize();
        int y = grid.getY() + grid.getSize();
        return sceneMap.getGridByGridPoint(x, y);
    }),
    /** 东南 **/
    SOUTH_EAST((sceneMap, grid) -> {
        int x = grid.getX() + grid.getSize();
        int y = grid.getY() - grid.getSize();
        return sceneMap.getGridByGridPoint(x, y);
    }),
    /** 西南 **/
    SOUTH_WEST((sceneMap, grid) -> {
        int x = grid.getX() - grid.getSize();
        int y = grid.getY() - grid.getSize();
        return sceneMap.getGridByGridPoint(x, y);
    })

    ;

    /** 根据格子计算出指定方向格子 **/
    private final BiFunction<SceneMap, Grid, Grid> function;

    Direction(BiFunction<SceneMap, Grid, Grid> function) {
        this.function = function;
    }

    public Grid calculateNextGrid(SceneMap map, Grid grid) {
        return function.apply(map, grid);
    }

}

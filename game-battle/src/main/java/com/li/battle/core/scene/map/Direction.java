package com.li.battle.core.scene.map;

import java.util.function.BiFunction;

/**
 * 方向枚举
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

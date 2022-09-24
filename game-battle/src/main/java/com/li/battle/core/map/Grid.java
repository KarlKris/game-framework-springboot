package com.li.battle.core.map;

import lombok.Getter;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

import java.util.Objects;

/**
 * 地图格子,左下方点作为格子坐标
 * @author li-yuanwen
 * @date 2022/6/6
 */
@Getter
public class Grid {

    public static final String SEPARATOR = "_";

    /** 左下方横坐标 **/
    private final int x;
    /** 左下方纵坐标 **/
    private final int y;
    /** 格子大小 **/
    private final int size;

    public Grid(int x, int y) {
        this(x, y, 1);
    }

    public Grid(int x, int y, int size) {
        this.x = x;
        this.y = y;
        this.size = size;
    }


    /**
     * 获取格子中心点Vector2D
     * @return 格子中心点Vector2D
     */
    public Vector2D getCenterPosition() {
        double width = size / (2d);
        return new Vector2D(x + width, y + width);
    }



    public String id() {
        return toId(x, y);
    }

    public static String toId(int x, int y) {
        return x + SEPARATOR + y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Grid grid = (Grid) o;
        return x == grid.x && y == grid.y && size == grid.size;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, size);
    }
}

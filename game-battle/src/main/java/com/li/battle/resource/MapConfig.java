package com.li.battle.resource;

import com.li.battle.core.map.Point;
import com.li.common.resource.anno.ResourceId;
import com.li.common.resource.anno.ResourceObj;
import lombok.Getter;

import java.util.List;

/**
 * 地图配置
 * @author li-yuanwen
 * @date 2022/6/7
 */
@Getter
@ResourceObj
public class MapConfig {

    /** 地图唯一标识 **/
    @ResourceId
    private int id;
    /** 地图长度 **/
    private int horizontalLength;
    /** 地图宽度 **/
    private int verticalLength;
    /** 格子长度 **/
    private int gridSize;
    /** 可走格子坐标点 **/
    private List<Point> grids;

}

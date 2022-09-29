package com.li.battle.resource;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import com.li.battle.core.map.Point;
import com.li.common.resource.anno.*;
import lombok.Getter;

import java.util.*;

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


    public static void main(String[] args) throws JsonProcessingException {
        int x = 10;
        int y = 10;
        List<Point> grids = new ArrayList<>(x * y);
        for (int i = 0; i <= x; i++) {
            for (int j = 0; j <= y; j++) {
                grids.add(new Point(i * 1000, j * 1000));
            }
        }
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.READ_ENUMS_USING_TO_STRING, true);
        System.out.println(mapper.writeValueAsString(grids));
    }

}

package com.li.battle.core.unit;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

/**
 * 所有Unit单位必须继承的接口,表位置,即每个单位都有位置
 * @author li-yuanwen
 * @date 2022/5/25
 */
public interface IPosition {

    /**
     * 获取当前位置矢量
     * @return 当前位置矢量
     */
    Vector2D getPosition();

}

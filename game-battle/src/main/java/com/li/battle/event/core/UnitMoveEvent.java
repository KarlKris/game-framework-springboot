package com.li.battle.event.core;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

/**
 * 移动事件
 * @author li-yuanwen
 * @date 2022/7/13
 */
public class UnitMoveEvent implements BattleEvent {

    /** 单位唯一ID **/
    private final long unitId;
    /** 原位置 **/
    private final Vector2D originPosition;
    /** 现位置 **/
    private final Vector2D nowPosition;

    public UnitMoveEvent(long unitId, Vector2D originPosition, Vector2D nowPosition) {
        this.unitId = unitId;
        this.originPosition = originPosition;
        this.nowPosition = nowPosition;
    }

    @Override
    public BattleEventType getType() {
        return BattleEventType.MOVE;
    }

    @Override
    public long getSource() {
        return unitId;
    }
}

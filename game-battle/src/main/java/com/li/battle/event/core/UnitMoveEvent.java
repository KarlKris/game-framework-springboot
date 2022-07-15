package com.li.battle.event.core;

/**
 * 移动事件
 * @author li-yuanwen
 * @date 2022/7/13
 */
public class UnitMoveEvent implements BattleEvent {

    /** 单位唯一ID **/
    private final long unitId;

    public UnitMoveEvent(long unitId) {
        this.unitId = unitId;
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

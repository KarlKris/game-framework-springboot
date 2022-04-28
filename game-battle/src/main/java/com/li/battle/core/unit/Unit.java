package com.li.battle.core.unit;

import com.li.battle.core.unit.model.UnitState;

/**
 * 单位接口
 * @author li-yuanwen
 * @date 2022/4/24
 */
public interface Unit {

    /**
     * 获取单元唯一标识
     * @return 单元id
     */
    long getId();

    /**
     * 获取单元当前状态
     * @return 单元当前状态
     */
    UnitState getState();

    /**
     * 修改状态
     * @param state 新状态
     */
    void modifyState(UnitState state);

}

package com.li.battle.core.unit;

import com.li.battle.core.unit.model.Attribute;
import com.li.battle.core.unit.model.FightUnitState;
import com.li.battle.core.unit.model.FightUnitType;

/**
 * 战斗单元对外接口
 * @author li-yuanwen
 */
public interface FightUnit {


    /**
     * 获取战斗单元唯一标识
     * @return 战斗单元id
     */
    long getId();

    /**
     * 获取战斗单元类型
     * @return 战斗单元类型
     */
    FightUnitType getUnitType();

    /**
     * 获取属性值
     * @param attribute 属性类型
     * @return 属性值
     */
    long getAttributeValue(Attribute attribute);

    /**
     * 修改属性
     * @param attribute 属性类型
     * @param value 更新值
     */
    void modifyAttribute(Attribute attribute, long value);

    /**
     * 获取战斗单元当前状态
     * @return 战斗单元当前状态
     */
    FightUnitState getState();

    /**
     * 修改状态
     * @param state 新状态
     */
    void modifyState(FightUnitState state);

}

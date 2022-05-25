package com.li.battle.core.unit;

import com.li.battle.core.UnitState;
import com.li.battle.core.UnitType;
import com.li.battle.core.scene.BattleScene;

/**
 * 单位接口
 * @author li-yuanwen
 * @date 2022/4/24
 */
public interface Unit extends IPosition {

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

    /**
     * 获取单位半径(即把单位看作半径为radius的圆柱体)
     * @return 单位半径
     */
    double getRadius();

    /**
     * 获取单元类型
     * @return 单元类型
     */
    UnitType getUnitType();


    /**
     * 获取单位关联的战斗场景
     * @return 战斗场景
     */
    BattleScene getScene();


}

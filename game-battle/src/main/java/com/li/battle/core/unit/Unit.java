package com.li.battle.core.unit;

import com.li.battle.collision.*;
import com.li.battle.core.*;
import com.li.battle.core.scene.BattleScene;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

/**
 * 单位接口
 * @author li-yuanwen
 * @date 2022/4/24
 */
public interface Unit extends IPosition, ICircle {

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
     * 获取单元类型
     * @return 单元类型
     */
    UnitType getUnitType();

    /**
     * 获取所在阵营
     * @return 阵营
     */
    CampType getCampType();

    /**
     * 获取单位关联的战斗场景
     * @return 战斗场景
     */
    BattleScene getScene();

    @Override
    default Vector2D getCentre() {
        return getPosition();
    }
}

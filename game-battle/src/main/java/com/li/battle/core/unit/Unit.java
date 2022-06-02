package com.li.battle.core.unit;

import com.li.battle.core.CampType;
import com.li.battle.core.UnitState;
import com.li.battle.core.UnitType;
import com.li.battle.core.scene.BattleScene;
import com.li.battle.util.Shape;

/**
 * 单位接口
 * @author li-yuanwen
 * @date 2022/4/24
 */
public interface Unit extends IPosition, Shape {

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
     * 获取所在阵营
     * @return 阵营
     */
    CampType getCampType();

    /**
     * 获取单位关联的战斗场景
     * @return 战斗场景
     */
    BattleScene getScene();

    /**
     * 战斗单元形状是圆形,获取横坐标最小值
     * @return 横坐标最小值
     */
    @Override
    default double getLeft() {
        return getPosition().getX() - getRadius();
    }

    /**
     * 战斗单元形状是圆形,获取横坐标最大值
     * @return 横坐标最大值
     */
    @Override
    default double getRight() {
        return getPosition().getX() + getRadius();
    }

    /**
     * 战斗单元形状是圆形,获取纵坐标最大值
     * @return 纵坐标最大值
     */
    @Override
    default double getTop() {
        return getPosition().getY() + getRadius();
    }

    /**
     * 战斗单元形状是圆形,获取纵坐标最小值
     * @return 纵坐标最小值
     */
    @Override
    default double getBottom() {
        return getPosition().getY() - getRadius();
    }
}

package com.li.battle.selector;

import com.li.battle.collision.Shape;
import com.li.battle.core.scene.BattleScene;
import com.li.battle.core.unit.FightUnit;
import com.li.battle.core.unit.IPosition;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 形状范围
 * @author li-yuanwen
 * @date 2022/6/1
 */
public class ShapeSelectorResult implements SelectorResult {

    /** 关联的战斗场景 **/
    private final BattleScene scene;

    /** 形状描述 **/
    private final Shape shape;

    public ShapeSelectorResult(BattleScene scene, Shape shape) {
        this.scene = scene;
        this.shape = shape;
    }

    @Override
    public List<IPosition> getResults() {
        // 可能产生碰撞的战斗单位集
        List<FightUnit> units = scene.distributed().retrieve(shape);
        // 碰撞检测
        return units.stream()
                .filter(shape::isCollision)
                .map(unit -> (IPosition) unit)
                .collect(Collectors.toList());
    }
}

package com.li.battle.selector;

import com.li.battle.core.unit.FightUnit;
import com.li.battle.resource.SelectorConfig;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.springframework.stereotype.Component;

/**
 * 玩家在指定的方向上释放技能
 * @author li-yuanwen
 * @date 2022/7/12
 */
@Component
public class DirectionSelector implements Selector {

    @Override
    public SelectorType getType() {
        return SelectorType.DIRECTION;
    }

    @Override
    public SelectorResult select(FightUnit unit, SelectorConfig config, SelectParam param, int range) {
        Vector2D position = new Vector2D(param.getDirectionX(), param.getDirectionY());
        if (range > 0) {
            // 施法范围内的位置
            position = position.subtract(unit.getPosition()).normalize().scalarMultiply(range);
        }
        return new PositionSelectorResult(position);
    }
}

package com.li.battle.selector;

import com.li.battle.core.scene.BattleScene;
import com.li.battle.core.unit.FightUnit;
import com.li.battle.resource.SelectorConfig;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.springframework.stereotype.Component;

import java.util.Collections;

/**
 * 单个目标选择器
 * @author li-yuanwen
 * @date 2022/5/31
 */
@Component
public class TargetSelector implements Selector {

    @Override
    public SelectorType getType() {
        return SelectorType.SINGLE_TARGET;
    }

    @Override
    public SelectorResult select(FightUnit unit, SelectorConfig config, SelectParam param, int range) {
        BattleScene scene = unit.getScene();
        FightUnit fightUnit = scene.getFightUnit(param.getTarget());
        if (fightUnit == null) {
            // todo 后续决定是否转BadRequestException
            throw new RuntimeException("目标不存在");
        }

        double distance = Vector2D.distance(unit.getPosition(), fightUnit.getPosition()) - unit.getRadius() - fightUnit.getRadius();
        if (distance > range) {
            // todo 后续决定是否转BadRequestException
            throw new RuntimeException("超出选择范围");
        }

        return new DefaultSelectorResult(Collections.singletonList(fightUnit));
    }
}

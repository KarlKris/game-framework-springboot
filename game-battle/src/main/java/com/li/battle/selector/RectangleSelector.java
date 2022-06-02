package com.li.battle.selector;

import com.li.battle.core.unit.FightUnit;
import com.li.battle.resource.SelectorConfig;
import com.li.battle.util.Rectangle;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.springframework.stereotype.Component;

/**
 * 矩形范围选择器
 * @author li-yuanwen
 * @date 2022/6/1
 */
@Component
public class RectangleSelector implements Selector {

    @Override
    public SelectorType getType() {
        return SelectorType.RECTANGLE;
    }

    @Override
    public SelectorResult select(FightUnit unit, SelectorConfig config, SelectParam param, int range) {
        if (param.getX() == param.getDirectionX() && param.getY() == param.getDirectionY()) {
            throw new RuntimeException("技能释放方向非法");
        }

        Vector2D start = new Vector2D(param.getX(), param.getY());
        // 判断施法距离
        if (Vector2D.distance(start, unit.getPosition()) > range) {
            // todo 施法距离
            throw new RuntimeException("超过技能施法距离");
        }

        // 目标终点
        Vector2D targetEnd = new Vector2D(param.getDirectionX(), param.getDirectionY());
        // 实际终点
        Vector2D end = targetEnd.subtract(start).normalize().scalarMultiply(config.getRange()).add(start);

        return new ShapeSelectorResult(unit.getScene(), new Rectangle(start, end, config.getWidth()));
    }
}

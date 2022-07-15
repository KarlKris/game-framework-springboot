package com.li.battle.selector;

import com.li.battle.core.unit.IPosition;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

import java.util.Collections;
import java.util.List;

/**
 * 目标位置
 * @author li-yuanwen
 * @date 2022/7/12
 */
public class PositionSelectorResult implements SelectorResult, IPosition {

    /** 目标位置 **/
    private final Vector2D position;

    public PositionSelectorResult(Vector2D position) {
        this.position = position;
    }

    @Override
    public List<IPosition> getResults() {
        return Collections.singletonList(this);
    }

    @Override
    public Vector2D getPosition() {
        return position;
    }
}

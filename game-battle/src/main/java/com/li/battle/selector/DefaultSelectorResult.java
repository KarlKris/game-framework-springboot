package com.li.battle.selector;

import com.li.battle.core.unit.FightUnit;
import com.li.battle.core.unit.IPosition;

import java.util.ArrayList;
import java.util.List;

/**
 * SelectorResult的默认实现
 * @author li-yuanwen
 * @date 2022/5/31
 */
public class DefaultSelectorResult implements SelectorResult {

    private final List<IPosition> units;

    public DefaultSelectorResult(List<FightUnit> units) {
        this.units = new ArrayList<>(units);
    }

    @Override
    public List<IPosition> getResults() {
        return units;
    }
}

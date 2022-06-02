package com.li.battle.selector;

import com.li.battle.core.unit.FightUnit;
import com.li.battle.resource.SelectorConfig;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;

/**
 * 己方阵营全部战斗单位选择器
 * @author li-yuanwen
 * @date 2022/5/31
 */
@Component
public class SelfCampSelector implements Selector {

    @Override
    public SelectorType getType() {
        return SelectorType.SELF_CAMP;
    }

    @Override
    public SelectorResult select(FightUnit unit, SelectorConfig config, SelectParam param, int range) {
        List<FightUnit> units = new LinkedList<>();
        for (FightUnit fightUnit : unit.getScene().getUnits()) {
            if (fightUnit.getCampType() != unit.getCampType()) {
                continue;
            }
            units.add(fightUnit);
        }
        return new DefaultSelectorResult(units);
    }
}

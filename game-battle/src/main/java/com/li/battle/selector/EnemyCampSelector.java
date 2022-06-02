package com.li.battle.selector;

import com.li.battle.core.CampType;
import com.li.battle.core.unit.FightUnit;
import com.li.battle.resource.SelectorConfig;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;

/**
 * 敌方阵营全部战斗单位选择器
 * @author li-yuanwen
 * @date 2022/5/31
 */
@Component
public class EnemyCampSelector implements Selector {

    @Override
    public SelectorType getType() {
        return SelectorType.ENEMY_CAMP;
    }

    @Override
    public SelectorResult select(FightUnit unit, SelectorConfig config, SelectParam param, int range) {
        if (unit.getCampType() == CampType.NEUTRAL) {
            return SelectorResult.EMPTY;
        }
        List<FightUnit> units = new LinkedList<>();
        for (FightUnit fightUnit : unit.getScene().getUnits()) {
            // 目前只有3个阵营,后续增加阵营类型的话,需要同步修改
            if (fightUnit.getCampType() == unit.getCampType()
                    || fightUnit.getCampType() == CampType.NEUTRAL) {
                continue;
            }
            units.add(fightUnit);
        }
        return new DefaultSelectorResult(units);
    }
}

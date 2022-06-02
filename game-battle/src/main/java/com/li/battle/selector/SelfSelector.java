package com.li.battle.selector;

import com.li.battle.core.unit.FightUnit;
import com.li.battle.resource.SelectorConfig;
import org.springframework.stereotype.Component;

import java.util.Collections;

/**
 * 自身选择器
 * @author li-yuanwen
 * @date 2022/6/1
 */
@Component
public class SelfSelector implements Selector {

    @Override
    public SelectorType getType() {
        return SelectorType.SELF;
    }

    @Override
    public SelectorResult select(FightUnit unit, SelectorConfig config, SelectParam param, int range) {
        return new DefaultSelectorResult(Collections.singletonList(unit));
    }
}

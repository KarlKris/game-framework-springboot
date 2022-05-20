package com.li.battle.selector.core;

import com.li.battle.core.unit.Unit;

import java.util.List;

/**
 * 选择器选择出的目标
 * @author li-yuanwen
 * @date 2021/10/20
 */
public interface SelectorTarget {

    /**
     * 计算目标命中的单位
     * @return 命中的单位集
     */
    List<Unit> calculateHitUnits();

}

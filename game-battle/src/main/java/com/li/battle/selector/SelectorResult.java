package com.li.battle.selector;

import com.li.battle.core.unit.IPosition;

import java.util.Collections;
import java.util.List;

/**
 * 选择器选择出的目标
 * @author li-yuanwen
 * @date 2021/10/20
 */
public interface SelectorResult {

    /** 空结果集 **/
    SelectorResult EMPTY = new DefaultSelectorResult(Collections.emptyList());

    /**
     * 计算目标命中的单位
     * @return 命中的单位集
     */
    List<IPosition> getResults();

}

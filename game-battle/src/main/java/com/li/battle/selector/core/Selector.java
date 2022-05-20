package com.li.battle.selector.core;

/**
 * 目标选择器
 * @author li-yuanwen
 */
public interface Selector {


    /**
     * 目标选择器类型
     * @return 目标选择器类型
     */
    SelectorType getType();


    /**
     * 目标选择
     * @return 选择结果
     */
    SelectorTarget select();

}

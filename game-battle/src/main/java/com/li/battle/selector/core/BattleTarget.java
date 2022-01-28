package com.li.battle.selector.core;

/**
 * 战斗中的目标
 * @author li-yuanwen
 * @date 2021/10/20
 */
public interface BattleTarget {

    /**
     * 检查是否命中
     * @return true 命中
     */
    boolean checkHit();

}

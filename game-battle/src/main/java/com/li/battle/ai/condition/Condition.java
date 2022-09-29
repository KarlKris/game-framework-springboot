package com.li.battle.ai.condition;

import com.li.battle.ai.behaviour.Behaviour;
import com.li.battle.ai.blackboard.BlackBoard;

/**
 * 条件，另一种行为树叶子节点，是行为树查看游戏世界信息的主要途径。
 * @author li-yuanwen
 * @date 2022/1/25
 */
public interface Condition extends Behaviour {

    /**
     * 条件是否满足
     * @param board 黑板
     * @return 条件是否满足
     */
    boolean valid(BlackBoard board);

}

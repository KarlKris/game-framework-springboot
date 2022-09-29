package com.li.battle.ai;

import com.li.battle.ai.behaviour.Behaviour;
import com.li.battle.ai.blackboard.BlackBoard;
import com.li.battle.core.unit.FightUnit;

/**
 * 行为树
 * @author li-yuanwen
 * @date 2022/1/25
 */
public final class BehaviourTree {

    /** 行为树根节点 **/
    private final Behaviour root;
    /** 黑板 **/
    private final BlackBoard board;

    public BehaviourTree(Behaviour root, FightUnit unit) {
        this.root = root;
        this.board = new BlackBoard(unit);
    }

    public void start() {
        this.root.tick(board);
    }

}

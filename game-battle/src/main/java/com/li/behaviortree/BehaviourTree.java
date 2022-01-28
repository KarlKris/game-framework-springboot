package com.li.behaviortree;

import com.li.behaviortree.behaviour.Behaviour;

/**
 * 行为树
 * @author li-yuanwen
 * @date 2022/1/25
 */
public final class BehaviourTree {

    /** 行为树根节点 **/
    private Behaviour root;

    BehaviourTree(Behaviour root) {
        this.root = root;
    }

    public void start() {
        this.root.tick();
    }

}

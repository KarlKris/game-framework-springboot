package com.li.battle.ai;

import com.li.battle.ai.behaviour.Behaviour;
import com.li.battle.ai.composite.Composite;

import java.util.Stack;

/**
 * 行为树构建类
 * @author li-yuanwen
 * @date 2022/1/25
 */
public class BehaviourTreeBuilder {

    /** 栈 **/
    private Stack<Behaviour> stack;
    /** 行为树根节点 **/
    private Behaviour root;

    public BehaviourTreeBuilder root(Behaviour root) {
        initOrReset();
        this.root = root;
        // 入栈
        stack.push(root);
        return this;
    }

    public BehaviourTreeBuilder addBehaviour(Behaviour behaviour) {
        if (this.root == null) {
            throw new RuntimeException("root behaviour is null");
        }

        Behaviour peek = stack.peek();
        if (peek instanceof Composite) {
            // 加入子行为
            ((Composite) peek).addChild(behaviour);
            // 入栈
            stack.push(behaviour);
        } else {
            throw new RuntimeException("last behaviour is not Composite");
        }

        return this;
    }

    public BehaviourTreeBuilder back() {
        stack.pop();
        return this;
    }

    public BehaviourTree build() {
        return new BehaviourTree(root);
    }

    private void initOrReset() {
        this.root = null;
        this.stack = new Stack<>();
    }

}

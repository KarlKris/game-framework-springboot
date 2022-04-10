package com.li.battle.ai.decorator;

import com.li.battle.ai.behaviour.AbstractBehaviour;
import com.li.battle.ai.behaviour.Behaviour;

/**
 * 抽象装饰器 控制节点
 * @author li-yuanwen
 * @date 2022/1/25
 */
public abstract class AbstractDecorator extends AbstractBehaviour implements Decorator {

    protected Behaviour behaviour;

    @Override
    public void setBehaviour(Behaviour behaviour) {
        this.behaviour = behaviour;
    }
}

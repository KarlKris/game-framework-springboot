package com.li.battle.ai.composite;

import com.li.battle.ai.behaviour.AbstractBehaviour;
import com.li.battle.ai.behaviour.Behaviour;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * 基础复合行为  控制节点
 * @author li-yuanwen
 * @date 2022/1/25
 */
public abstract class AbstractComposite extends AbstractBehaviour implements Composite {

    /** 子节点 **/
    private final List<Behaviour> children = new LinkedList<>();

    @Override
    public void addChild(Behaviour behaviour) {
        children.add(behaviour);
    }

    @Override
    public void removeChild(Behaviour behaviour) {
        children.remove(behaviour);
    }

    @Override
    public void clearChildren() {
        children.clear();
    }

    @Override
    public List<Behaviour> getChildren() {
        return Collections.unmodifiableList(children);
    }
}

package com.li.battle.ai.composite;

import com.li.battle.ai.behaviour.Behaviour;

import java.util.List;

/**
 * 复合行为,行为树中具有多个子节点的分支被称为复合行为
 * @author li-yuanwen
 * @date 2022/1/25
 */
public interface Composite extends Behaviour {

    /**
     * 添加子节点
     * @param behaviour /
     */
    void addChild(Behaviour behaviour);

    /**
     * 移除子行为
     * @param behaviour /
     */
    void removeChild(Behaviour behaviour);

    /**
     * 清除所有子行为
     */
    void clearChildren();

    /**
     * 获取所有子行为
     * @return /
     */
    List<Behaviour> getChildren();

}

package com.li.battle.ai.action;

import com.li.battle.ai.behaviour.Behaviour;

/**
 * 抽象动作的概念，尝试改变游戏状态的样子行为被称为动作。
 * 在行为树中，叶子节点负责从游戏世界访问信息并对游戏世界造成改变。
 * @author li-yuanwen
 * @date 2022/1/25
 */
public interface Action extends Behaviour {

    /**
     * 初始化
     */
    void init();

    /**
     * 关闭
     */
    void close();

}

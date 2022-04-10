package com.li.battle.ai.decorator;

import com.li.battle.ai.behaviour.Behaviour;

/**
 * 装饰器,对行为进行包装，在其原有逻辑基础上增添细节和细微的差别
 * @author li-yuanwen
 * @date 2022/1/25
 */
public interface Decorator extends Behaviour {

    /**
     * 设置装饰的行为
     * @param behaviour /
     */
    void setBehaviour(Behaviour behaviour);

}

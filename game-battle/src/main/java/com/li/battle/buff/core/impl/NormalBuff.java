package com.li.battle.buff.core.impl;

import com.li.battle.buff.core.AbstractBuff;
import com.li.battle.core.context.AbstractContext;
import com.li.battle.resource.BuffConfig;

/**
 * 普通buff对象,即不会修改状态,属性,运动的Buff
 * 例如LOL眼的效果就可以做成Buff,广播给相关玩家
 * @author li-yuanwen
 * @date 2022/5/25
 */
public class NormalBuff extends AbstractBuff {

    /** Buff创建上下文 **/
    private final AbstractContext context;

    public NormalBuff(BuffConfig config, long caster, long parent, int skillId, AbstractContext context) {
        super(config, caster, parent, skillId, context);
        this.context = context;
    }

    @Override
    public AbstractContext getContext() {
        return context;
    }
}

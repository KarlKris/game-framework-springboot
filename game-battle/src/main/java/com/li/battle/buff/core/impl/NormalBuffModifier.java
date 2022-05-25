package com.li.battle.buff.core.impl;

import com.li.battle.buff.core.BuffModifier;
import com.li.battle.core.context.AbstractDamageAlterContext;
import com.li.battle.resource.BuffConfig;

/**
 * 默认BuffModifier实现
 * @author li-yuanwen
 * @date 2022/5/25
 */
public class NormalBuffModifier extends BuffModifier {

    /** Buff创建上下文 **/
    protected final AbstractDamageAlterContext context;

    public NormalBuffModifier(BuffConfig config, long caster, long parent
            , int skillId, AbstractDamageAlterContext context) {
        super(config, caster, parent, skillId, context);
        this.context = context;
    }

    @Override
    public AbstractDamageAlterContext getContext() {
        return context;
    }

}

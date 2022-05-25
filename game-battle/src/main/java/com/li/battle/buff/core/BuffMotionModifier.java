package com.li.battle.buff.core;

import com.li.battle.core.context.AbstractDamageAlterContext;
import com.li.battle.resource.BuffConfig;

/**
 * 继承于BuffModifier,代表此类Buff提供修改玩家运动效果的功能
 * @author li-yuanwen
 */
public abstract class BuffMotionModifier extends BuffModifier {

    public BuffMotionModifier(BuffConfig config, long caster, long parent
            , int skillId, AbstractDamageAlterContext context) {
        super(config, caster, parent, skillId, context);
    }

    /** 修改运动 **/
    abstract void modifyMotion();

}

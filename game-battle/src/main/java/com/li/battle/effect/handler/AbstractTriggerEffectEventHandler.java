package com.li.battle.effect.handler;

import com.li.battle.effect.TriggerEffect;
import com.li.battle.event.core.BattleEvent;
import com.li.battle.event.handler.AbstractEventHandler;

/**
 * 基于TriggerEffect作为EventReceiver的事件处理器
 * @author li-yuanwen
 * @date 2022/5/24
 */
public abstract class AbstractTriggerEffectEventHandler<E extends BattleEvent> extends AbstractEventHandler<TriggerEffect, E> {

}

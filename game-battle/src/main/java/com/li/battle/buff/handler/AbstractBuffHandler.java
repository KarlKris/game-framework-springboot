package com.li.battle.buff.handler;

import com.li.battle.buff.core.Buff;
import com.li.battle.event.handler.AbstractEventHandler;
import com.li.battle.event.core.BattleEvent;

/**
 * buff类事件处理器基类
 * @author li-yuanwen
 * @date 2022/5/23
 */
public abstract class AbstractBuffHandler<B extends Buff, E extends BattleEvent> extends AbstractEventHandler<B, E> {


}

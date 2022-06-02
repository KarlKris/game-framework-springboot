package com.li.battle.trigger.handler;

import com.li.battle.buff.core.Buff;
import com.li.battle.core.scene.BattleScene;
import com.li.battle.core.unit.FightUnit;
import com.li.battle.effect.Effect;
import com.li.battle.event.EventHandlerContext;
import com.li.battle.event.core.BattleEvent;
import com.li.battle.event.handler.AbstractEventHandler;
import com.li.battle.trigger.TriggerReceiver;
import com.li.battle.trigger.core.Trigger;

/**
 * 触发器类事件处理器
 * @author li-yuanwen
 * @date 2022/5/26
 */
public abstract class AbstractTriggerHandler<E extends BattleEvent> extends AbstractEventHandler<TriggerReceiver, E> {

    @Override
    public void handle(EventHandlerContext context, Object receiver, Object event) {
        if (accept(receiver, event)) {
            TriggerReceiver castReceiver = (TriggerReceiver) receiver;
            E castEvent = (E) event;
            handle0(context, castReceiver, castEvent);

        }

        context.fireHandleEvent(event);
    }

    @Override
    protected void handle0(EventHandlerContext context, TriggerReceiver receiver, E event) {
        // 判断是否是自己释放的技能
        if (receiver.getUnitId() != event.getSource()) {
            return;
        }

        // 判断triggerCD
        if (receiver.isInCoolDown()) {
            return;
        }

        Trigger trigger = receiver.getTrigger();
        trigger.tryTrigger(receiver.getUnitId(), receiver.getTarget(), event, triggerId -> {
            // 触发成功,更新CD
            receiver.afterExecuteEffect();
            // 执行效果
            BattleScene scene = receiver.getScene();
            FightUnit caster = scene.getFightUnit(receiver.getUnitId());
            FightUnit target = scene.getFightUnit(triggerId);
            for (Effect<Buff> effect : receiver.getConfig().getTriggerEffects()) {
                effect.onAction(caster, target, receiver);
            }
        });

    }


}

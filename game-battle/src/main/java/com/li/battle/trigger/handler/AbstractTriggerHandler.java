package com.li.battle.trigger.handler;

import cn.hutool.core.util.ArrayUtil;
import com.li.battle.core.scene.BattleScene;
import com.li.battle.core.unit.FightUnit;
import com.li.battle.effect.EffectExecutor;
import com.li.battle.effect.domain.EffectParam;
import com.li.battle.effect.source.TriggerEffectSource;
import com.li.battle.event.EventHandlerContext;
import com.li.battle.event.core.BattleEvent;
import com.li.battle.event.handler.AbstractEventHandler;
import com.li.battle.trigger.Trigger;
import lombok.extern.slf4j.Slf4j;

/**
 * 触发器类事件处理器
 * @author li-yuanwen
 * @date 2022/5/26
 */
@Slf4j
public abstract class AbstractTriggerHandler<TR extends Trigger, E extends BattleEvent> extends AbstractEventHandler<TR, E> {

    @Override
    protected void handle0(EventHandlerContext context, TR receiver, E event) {
        // 判断是否是自己释放的技能
        if (receiver.getUnitId() != event.getSource()) {
            return;
        }

        // 判断triggerCD
        if (receiver.isInCoolDown()) {
            return;
        }

        doHandle(receiver, event);
    }


    /**
     * 触发器处理事件
     * @param receiver 触发器
     * @param event 事件
     */
    protected abstract void doHandle(TR receiver, E event);

    /**
     * 执行触发器效果
     * @param receiver 触发器
     * @param target 目标
     */
    protected void executeEffect(Trigger receiver, FightUnit target) {

        if (log.isDebugEnabled()) {
            log.debug("单位[{}]的触发器[{}]触发效果", receiver.getOwner(), receiver.getClass().getSimpleName());
        }

        // 触发成功,更新CD
        receiver.afterExecuteEffect();

        if (ArrayUtil.isEmpty(receiver.getConfig().getTriggerEffects())) {
            return;
        }

        BattleScene scene = receiver.battleScene();
        EffectExecutor effectExecutor = scene.battleSceneHelper().effectExecutor();
        TriggerEffectSource effectSource = new TriggerEffectSource(receiver, target);
        // 执行效果
        for (EffectParam effectParam : receiver.getConfig().getTriggerEffects()) {
            effectExecutor.execute(effectSource, effectParam);
        }
    }


}

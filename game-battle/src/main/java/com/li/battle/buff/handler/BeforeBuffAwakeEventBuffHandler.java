package com.li.battle.buff.handler;

import com.li.battle.buff.core.Buff;
import com.li.battle.effect.EffectExecutor;
import com.li.battle.effect.domain.EffectParam;
import com.li.battle.effect.source.BuffEventEffectSource;
import com.li.battle.event.EventHandlerContext;
import com.li.battle.event.core.*;
import org.springframework.stereotype.Component;

/**
 * 监听buff生效之前事件处理器
 * @author li-yuanwen
 * @date 2022/10/9
 */
@Component
public class BeforeBuffAwakeEventBuffHandler extends AbstractBuffHandler<Buff, BeforeBuffAwakeEvent> {

    @Override
    protected void doHandle(EventHandlerContext context, Buff receiver, BeforeBuffAwakeEvent event) {
        EffectExecutor effectExecutor = receiver.battleScene().battleSceneHelper().effectExecutor();
        BuffEventEffectSource source = new BuffEventEffectSource(receiver, event.getEffectSource());
        for (EffectParam effectParam : receiver.getConfig().getAwakeEffects()) {
            effectExecutor.execute(source, effectParam);
        }
    }

    @Override
    public BattleEventType getEventType() {
        return BattleEventType.BEFORE_BUFF_AWAKE;
    }
}

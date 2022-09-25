package com.li.battle.buff.handler;

import com.li.battle.buff.core.Buff;
import com.li.battle.core.scene.BattleScene;
import com.li.battle.effect.EffectExecutor;
import com.li.battle.effect.domain.EffectParam;
import com.li.battle.event.EventHandlerContext;
import com.li.battle.event.EventReceiver;
import com.li.battle.event.core.BattleEvent;
import com.li.battle.event.core.BattleEventType;
import com.li.battle.event.core.BeforeDamageEvent;
import com.li.battle.resource.BuffConfig;
import org.springframework.stereotype.Component;

/**
 * buff监听BeforeDamageEvent事件执行效果
 * @author li-yuanwen
 * @date 2022/9/25
 */
@Component
public class BeforeDamageEventBuffHandler extends AbstractBuffHandler<Buff, BeforeDamageEvent> {

    @Override
    protected void handle0(EventHandlerContext context, Buff receiver, BeforeDamageEvent event) {
        BattleScene battleScene = receiver.battleScene();
        EffectExecutor effectExecutor = battleScene.battleSceneHelper().effectExecutor();

        long targetId = event.getTarget();
        long casterId = receiver.getCaster();
        if (targetId == casterId) {
            // 己方受到伤害
            BuffConfig buffConfig = receiver.getConfig();
            for (EffectParam param : buffConfig.getBeforeTakeDamageEffects()) {
                // todo 执行效果
            }
        } else if (event.getMaker() == casterId) {
            // 敌方受到伤害
            BuffConfig buffConfig = receiver.getConfig();
            for (EffectParam param : buffConfig.getBeforeDamageEffects()) {
                // todo 执行效果
            }

        }
    }


    @Override
    public BattleEventType getEventType() {
        return BattleEventType.BEFORE_DAMAGE;
    }
}

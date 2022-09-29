package com.li.battle.buff.handler;

import cn.hutool.core.util.ArrayUtil;
import com.li.battle.buff.core.Buff;
import com.li.battle.core.scene.BattleScene;
import com.li.battle.effect.EffectExecutor;
import com.li.battle.effect.domain.EffectParam;
import com.li.battle.effect.source.BuffEventEffectSource;
import com.li.battle.event.EventHandlerContext;
import com.li.battle.event.core.*;
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
    protected void doHandle(EventHandlerContext context, Buff receiver, BeforeDamageEvent event) {
        BattleScene battleScene = receiver.battleScene();
        EffectExecutor effectExecutor = battleScene.battleSceneHelper().effectExecutor();

        long targetId = event.getTarget();
        long casterId = receiver.getCaster();
        if (targetId == casterId) {
            // 己方受到伤害前
            BuffConfig buffConfig = receiver.getConfig();
            if (ArrayUtil.isEmpty(buffConfig.getBeforeTakeDamageEffects())) {
                return;
            }
            BuffEventEffectSource source = new BuffEventEffectSource(receiver, event.getEffectSource());
            for (EffectParam param : buffConfig.getBeforeTakeDamageEffects()) {
                // 执行效果
                effectExecutor.execute(source, param);
            }
        } else if (event.getMaker() == casterId) {
            // 敌方受到伤害前
            BuffConfig buffConfig = receiver.getConfig();
            if (ArrayUtil.isEmpty(buffConfig.getBeforeDamageEffects())) {
                return;
            }
            BuffEventEffectSource source = new BuffEventEffectSource(receiver, event.getEffectSource());
            for (EffectParam param : buffConfig.getBeforeDamageEffects()) {
                // 执行效果
                effectExecutor.execute(source, param);
            }
        }
    }


    @Override
    public BattleEventType getEventType() {
        return BattleEventType.BEFORE_DAMAGE;
    }
}

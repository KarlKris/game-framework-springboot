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
 * buff监听死亡前事件执行效果
 * @author li-yuanwen
 * @date 2022/9/26
 */
@Component
public class BeforeDeadEventBuffHandler extends AbstractBuffHandler<Buff, BeforeDeadEvent> {

    @Override
    protected void doHandle(EventHandlerContext context, Buff receiver, BeforeDeadEvent event) {
        long targetId = event.getTarget();
        if (targetId == receiver.getOwner()) {
            // 我方死亡前
            BattleScene battleScene = receiver.battleScene();
            EffectExecutor effectExecutor = battleScene.battleSceneHelper().effectExecutor();
            BuffConfig buffConfig = receiver.getConfig();
            if (ArrayUtil.isEmpty(buffConfig.getBeforeDeadEffects())) {
                return;
            }
            BuffEventEffectSource source = new BuffEventEffectSource(receiver, event.getEffectSource());
            for (EffectParam param : buffConfig.getBeforeDeadEffects()) {
                // 执行效果
                effectExecutor.execute(source, param);
            }
        }
    }

    @Override
    public BattleEventType getEventType() {
        return BattleEventType.BEFORE_DEAD;
    }
}

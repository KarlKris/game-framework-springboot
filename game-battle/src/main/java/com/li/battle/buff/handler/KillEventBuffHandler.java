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
 * buff监听击杀事件后执行效果
 * @author li-yuanwen
 * @date 2022/9/26
 */
@Component
public class KillEventBuffHandler extends AbstractBuffHandler<Buff, KillEvent> {

    @Override
    protected void doHandle(EventHandlerContext context, Buff receiver, KillEvent event) {
        BattleScene battleScene = receiver.battleScene();
        EffectExecutor effectExecutor = battleScene.battleSceneHelper().effectExecutor();

        long targetId = event.getTarget();
        long casterId = receiver.getCaster();
        if (targetId == casterId) {
            // 我方死亡后
            BuffConfig buffConfig = receiver.getConfig();
            if (ArrayUtil.isEmpty(buffConfig.getAfterDeadEffects())) {
                return;
            }
            BuffEventEffectSource source = new BuffEventEffectSource(receiver, event.getEffectSource());
            for (EffectParam param : buffConfig.getAfterDeadEffects()) {
                // 执行效果
                effectExecutor.execute(source, param);
            }
        } else if (event.getKiller() == casterId) {
            // 我方击杀目标后
            BuffConfig buffConfig = receiver.getConfig();
            if (ArrayUtil.isEmpty(buffConfig.getKillEffects())) {
                return;
            }
            BuffEventEffectSource source = new BuffEventEffectSource(receiver, event.getEffectSource());
            for (EffectParam param : buffConfig.getKillEffects()) {
                // 执行效果
                effectExecutor.execute(source, param);
            }
        }
    }

    @Override
    public BattleEventType getEventType() {
        return BattleEventType.KILL;
    }
}

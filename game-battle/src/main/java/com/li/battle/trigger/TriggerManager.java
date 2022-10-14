package com.li.battle.trigger;

import cn.hutool.core.util.ArrayUtil;
import com.li.battle.core.scene.BattleScene;
import com.li.battle.effect.*;
import com.li.battle.effect.domain.EffectParam;
import com.li.battle.effect.source.TriggerEffectSource;

import java.util.*;

/**
 * 触发器管理
 * @author li-yuanwen
 * @date 2022/5/18
 */
public class TriggerManager {

    /** 关联的战斗场景 **/
    private final BattleScene scene;

    /** 待处理的buff队列 **/
    private final PriorityQueue<Trigger> queue = new PriorityQueue<>(Comparator.comparingLong(Trigger::getExpireRound));

    public TriggerManager(BattleScene scene) {
        this.scene = scene;
    }


    public void addTriggerReceiver(Trigger trigger) {
        if (trigger.getExpireRound() != 0) {
            queue.offer(trigger);
        }

    }

    public void removeTriggerReceiver(long ownerId) {
        queue.removeIf(triggerReceiver -> triggerReceiver.getOwner() == ownerId);
    }

    public void update() {
        Trigger element = queue.peek();
        while (element != null && element.isExpire()) {
            queue.poll();
            handle(element);
            element = queue.peek();
        }
    }

    private void handle(Trigger receiver) {
        if (receiver.isManualInvalid()) {
            return;
        }

        TriggerEffectSource source = new TriggerEffectSource(receiver, null);
        EffectExecutor effectExecutor = scene.battleSceneHelper().effectExecutor();
        if (ArrayUtil.isEmpty(receiver.getConfig().getDestroyEffects())) {
            return;
        }
        for (EffectParam effectParam :  receiver.getConfig().getDestroyEffects()) {
            effectExecutor.execute(source, effectParam);
        }
    }
}

package com.li.battle.trigger;

import com.li.battle.core.scene.BattleScene;
import com.li.battle.effect.*;
import com.li.battle.effect.domain.EffectParam;
import com.li.battle.effect.source.TriggerReceiverEffectSource;

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
    private final PriorityQueue<TriggerReceiver> queue = new PriorityQueue<>(Comparator.comparingLong(TriggerReceiver::getExpireRound));

    public TriggerManager(BattleScene scene) {
        this.scene = scene;
    }


    public void addTriggerReceiver(TriggerReceiver triggerReceiver) {
        queue.offer(triggerReceiver);
    }

    public void removeTriggerReceiver(long ownerId) {
        queue.removeIf(triggerReceiver -> triggerReceiver.getOwner() == ownerId);
    }

    public void update() {
        TriggerReceiver element = queue.peek();
        long curRound = scene.getSceneRound();
        while (element != null && element.isInvalid(curRound)) {
            queue.poll();
            handle(element);
            element = queue.peek();
        }
    }

    private void handle(TriggerReceiver receiver) {
        if (receiver.isManualInvalid()) {
            return;
        }

        TriggerReceiverEffectSource source = new TriggerReceiverEffectSource(receiver, null);
        EffectExecutor effectExecutor = scene.battleSceneHelper().effectExecutor();
        for (EffectParam effectParam :  receiver.getConfig().getDestroyEffects()) {
            effectExecutor.execute(source, effectParam);
        }
    }
}

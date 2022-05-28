package com.li.battle.trigger;

import com.li.battle.buff.core.Buff;
import com.li.battle.core.scene.BattleScene;
import com.li.battle.core.unit.FightUnit;
import com.li.battle.effect.Effect;

import java.util.Comparator;
import java.util.PriorityQueue;

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

        FightUnit caster = scene.getFightUnit(receiver.getUnitId());
        FightUnit target = scene.getFightUnit(receiver.getTarget());
        for (Effect<Buff> effect :  receiver.getConfig().getDestroyEffects()) {
            effect.onAction(caster, target);
        }
    }
}

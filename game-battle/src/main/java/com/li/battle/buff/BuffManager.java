package com.li.battle.buff;

import cn.hutool.core.util.ArrayUtil;
import com.li.battle.buff.core.Buff;
import com.li.battle.core.scene.BattleScene;
import com.li.battle.core.unit.FightUnit;
import com.li.battle.effect.Effect;
import com.li.battle.resource.BuffConfig;

import java.util.Comparator;
import java.util.PriorityQueue;

/**
 * Buff管理
 * todo 考虑单位退出场景后相应的Buff删除问题
 * @author li-yuanwen
 * @date 2022/5/18
 */
public class BuffManager {

    /** 关联的场景 **/
    private final BattleScene scene;

    /** 待处理的buff队列 **/
    private final PriorityQueue<Buff> queue = new PriorityQueue<>(Comparator.comparingLong(Buff::getNextRound));


    public BuffManager(BattleScene scene) {
        this.scene = scene;
    }

    public boolean addBuff(Buff buff) {
        // todo 判断是否需要合并

        BuffConfig config = scene.battleSceneHelper().configHelper().getBuffConfigById(buff.getBuffId());
        if (ArrayUtil.isNotEmpty(config.getAwakeEffects())) {
            for (Effect effect : config.getAwakeEffects()) {
                effect.onAction(buff);
            }
        }

        queue.offer(buff);
        return true;
    }

    public void removeBuff(long ownerId) {
        queue.removeIf(buff -> buff.getOwner() == ownerId);
    }


    /**
     * 判断目标是否免疫buff
     * @param target buff挂载目标
     * @param buffTag buffTag
     * @return true 免疫
     */
    public boolean isImmuneTag(FightUnit target, byte buffTag) {
        // todo
        return false;
    }

    public void update() {
        Buff element = queue.peek();
        long curRound = scene.getSceneRound();
        while (element != null && element.getNextRound() <= curRound) {
            queue.poll();
            handle(element, curRound);
            element = queue.peek();
        }
    }

    private void handle(Buff buff, long curRound) {
        BuffConfig config = scene.battleSceneHelper().configHelper().getBuffConfigById(buff.getBuffId());
        if (!buff.isExpire(curRound)) {
            if (ArrayUtil.isNotEmpty(config.getThinkEffects())) {
                for (Effect<Buff> effect : config.getThinkEffects()) {
                    effect.onAction(buff);
                }
            }

            buff.updateNextRound(buff.getNextRound() + config.getThinkInterval() / scene.getRoundPeriod());
            queue.offer(buff);
        } else {
            // 执行销毁效果
            if (ArrayUtil.isNotEmpty(config.getDestroyEffects())) {
                for (Effect<Buff> effect : config.getDestroyEffects()) {
                    effect.onAction(buff);
                }
            }
        }

    }


}

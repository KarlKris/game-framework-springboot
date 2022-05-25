package com.li.battle.buff;

import cn.hutool.core.util.ArrayUtil;
import com.li.battle.buff.core.Buff;
import com.li.battle.core.scene.BattleScene;
import com.li.battle.core.unit.FightUnit;
import com.li.battle.effect.Effect;
import com.li.battle.resource.BuffConfig;

import java.util.PriorityQueue;

/**
 * Buff管理
 * @author li-yuanwen
 * @date 2022/5/18
 */
public class BuffManager {

    /** 关联的场景 **/
    private final BattleScene scene;

    /** 待处理的buff队列 **/
    private final PriorityQueue<BuffElement> queue = new PriorityQueue<>();


    public BuffManager(BattleScene scene) {
        this.scene = scene;
    }

    public void addBuff(Buff buff) {
        // todo 判断是否需要合并

        BuffConfig config = scene.getBattleSceneHelper().getConfigHelper().getBuffConfigById(buff.getBuffId());
        if (ArrayUtil.isNotEmpty(config.getStartEffects())) {
            for (Effect effect : config.getStartEffects()) {
                effect.onAction(buff);
            }
        }

        queue.offer(new BuffElement(buff.getNextRound(), buff));
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

    public void update(long curRound) {
        BuffElement element = queue.peek();
        while (element != null && element.round <= curRound) {
            queue.poll();
            handle(element.buff, curRound);
            element = queue.peek();
        }
    }

    private void handle(Buff buff, long curRound) {
        BuffConfig config = scene.getBattleSceneHelper().getConfigHelper().getBuffConfigById(buff.getBuffId());
        if (!buff.expire(curRound)) {
            if (ArrayUtil.isNotEmpty(config.getThinkEffects())) {
                for (Effect effect : config.getThinkEffects()) {
                    effect.onAction(buff);
                }
            }

            buff.updateNextRound(buff.getNextRound() + config.getThinkInterval() / scene.getRoundPeriod());
            queue.offer(new BuffElement(buff.getNextRound(), buff));
        } else {
            // 执行销毁效果
            if (ArrayUtil.isNotEmpty(config.getDestroyEffects())) {
                for (Effect effect : config.getDestroyEffects()) {
                    effect.onAction(buff);
                }
            }
        }

    }



    private static final class BuffElement {

        /** 执行的回合数 **/
        private final long round;
        /** 事件内容 **/
        private final Buff buff;

        public BuffElement(long round, Buff buff) {
            this.round = round;
            this.buff = buff;
        }
    }


}

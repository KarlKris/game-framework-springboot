package com.li.battle.buff;

import cn.hutool.core.util.ArrayUtil;
import com.li.battle.buff.core.Buff;
import com.li.battle.core.scene.BattleScene;
import com.li.battle.core.unit.FightUnit;
import com.li.battle.effect.EffectExecutor;
import com.li.battle.effect.domain.EffectParam;
import com.li.battle.effect.source.BuffEffectSource;
import com.li.battle.resource.BuffConfig;
import org.springframework.util.CollectionUtils;

import java.util.*;


/**
 * Buff管理
 * @author li-yuanwen
 * @date 2022/5/18
 */
public class BuffManager {

    /** 关联的场景 **/
    private final BattleScene scene;

    /** 待处理的buff队列 **/
    private final PriorityQueue<Buff> queue = new PriorityQueue<>(Comparator.comparingLong(Buff::getNextRound));

    /** buff字典 **/
    private final Map<Long, Buff> buffs = new HashMap<>();

    public BuffManager(BattleScene scene) {
        this.scene = scene;
    }

    public boolean addBuff(Buff buff) {
        // 判断是否需要合并
        if (buff.getConfig().getMergeRule().isMergeable()) {
            FightUnit unit = scene.getFightUnit(buff.getParent());
            int buffId = buff.getBuffId();
            List<Buff> buffs = unit.getBuffById(buffId);
            if (!CollectionUtils.isEmpty(buffs)) {
                final long caster = buff.getCaster();
                Optional<Buff> optional = buffs.stream().filter(b -> b.getCaster() == caster).findFirst();
                if (optional.isPresent()) {
                    Buff oldBuff = optional.get();
                    long oldNextRound = oldBuff.getNextRound();
                    oldBuff.onBuffRefresh(buff);
                    if (oldBuff.getNextRound() != oldNextRound) {
                        long oldBuffId = oldBuff.getId();
                        queue.removeIf(b -> b.getId() == oldBuffId);
                        queue.offer(oldBuff);
                    }
                    return true;
                }
            }
        }

        BuffConfig config = buff.getConfig();
        if (ArrayUtil.isNotEmpty(config.getStartEffects())) {
            EffectExecutor effectExecutor = buff.battleScene().battleSceneHelper().effectExecutor();
            BuffEffectSource source = new BuffEffectSource(buff);
            for (EffectParam effectParam : config.getStartEffects()) {
                effectExecutor.execute(source, effectParam);
            }
        }

        queue.offer(buff);
        buffs.put(buff.getId(), buff);
        return true;
    }

    public void removeUnitAllBuff(long ownerId) {
        Iterator<Buff> iterator = queue.iterator();
        while (iterator.hasNext()) {
            Buff buff = iterator.next();
            if (buff.getOwner() != ownerId) {
                continue;
            }
            iterator.remove();
            buffs.remove(buff.getId());
        }
    }

    public void removeBuff(Buff buff) {
        buffs.remove(buff.getId());
    }



    /**
     * 判断目标是否免疫buff
     * @param target buff挂载目标
     * @param caster buff释放者
     * @param buffTag buffTag
     * @return true 免疫
     */
    public boolean isImmuneTag(FightUnit target, long caster, int buffTag) {
        if (caster == target.getId()) {
            return false;
        }
        // 遍历目标身上的所有buff
        for (Buff buff : target.getAllBuffs()) {
            if (buff.isExpire()) {
                continue;
            }
            int b = buff.getConfig().getImmuneTag() & buffTag;
            if (b > 0) {
                return true;
            }
        }
        return false;
    }

    public void update() {
        Buff element = queue.peek();
        long curRound = scene.getSceneRound();
        while (element != null && element.getNextRound() <= curRound) {
            queue.poll();
            if (buffs.containsKey(element.getId())) {
                handle(element, curRound);
            }
            element = queue.peek();
        }
    }

    private void handle(Buff buff, long curRound) {
        BuffConfig config = scene.battleSceneHelper().configHelper().getBuffConfigById(buff.getBuffId());
        if (!buff.isExpire()) {
            if (ArrayUtil.isNotEmpty(config.getThinkEffects())) {
                EffectExecutor effectExecutor = scene.battleSceneHelper().effectExecutor();
                BuffEffectSource source = new BuffEffectSource(buff);
                for (EffectParam effectParam : config.getThinkEffects()) {
                    effectExecutor.execute(source, effectParam);
                }
                long nextRound = Math.max(buff.getNextRound() + config.getThinkInterval() / scene.getRoundPeriod(), curRound + 1);
                buff.updateNextRound(nextRound);
            } else {
                if (buff.getExpireRound() == 0) {
                    buff.updateNextRound(curRound + 1);
                } else {
                    buff.updateNextRound(buff.getExpireRound());
                }
            }

            queue.offer(buff);

        } else {
            buffs.remove(buff.getId());
            // 执行销毁效果
            if (ArrayUtil.isNotEmpty(config.getDestroyEffects())) {
                EffectExecutor effectExecutor = scene.battleSceneHelper().effectExecutor();
                BuffEffectSource source = new BuffEffectSource(buff);
                for (EffectParam effectParam : config.getDestroyEffects()) {
                    effectExecutor.execute(source, effectParam);
                }
            }
            // 移除单位身上的buff
            scene.getFightUnit(buff.getParent()).removeBuff(buff);

        }

    }


}

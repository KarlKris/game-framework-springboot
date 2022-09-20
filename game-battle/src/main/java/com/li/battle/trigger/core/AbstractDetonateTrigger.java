package com.li.battle.trigger.core;

import cn.hutool.core.util.ArrayUtil;
import com.li.battle.event.core.BattleEvent;
import com.li.battle.event.core.SkillExecutedEvent;
import com.li.battle.skill.BattleSkill;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 抽象引爆型Trigger
 * @author li-yuanwen
 * @date 2022/5/27
 */
@Getter
@NoArgsConstructor
public abstract class AbstractDetonateTrigger implements Trigger {

    /** 监听技能id集 **/
    protected int[] skillIds;
    /** 目标次数 **/
    protected int num;

    /** 当前叠加次数 **/
    protected int curNum;

    public AbstractDetonateTrigger(int[] skillIds, int num) {
        this.skillIds = skillIds;
        this.num = num;
    }

    @Override
    public void tryTrigger(long casterId,  BattleEvent event, TriggerSuccessCallback callback) {
        // 触发器释放方与事件来源方不一致,忽略
        if (casterId != event.getSource()) {
            return;
        }
        if (event instanceof SkillExecutedEvent) {
            SkillExecutedEvent e = (SkillExecutedEvent) event;
            BattleSkill skill = e.getSkill();
            if (!ArrayUtil.contains(skillIds, skill.getSkillId())) {
                return;
            }

            // 子类实现具体的逻辑判断
            try0(casterId, e, callback);
        }
    }

    /**
     * 子类实现具体的逻辑判断
     * @param casterId 触发器施法者
     * @param event 技能执行事件
     * @param callback 成功回调接口
     */
    protected abstract void try0(long casterId,  SkillExecutedEvent event, TriggerSuccessCallback callback);
}

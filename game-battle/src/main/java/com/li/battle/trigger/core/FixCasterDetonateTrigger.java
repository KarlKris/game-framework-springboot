package com.li.battle.trigger.core;

import com.li.battle.core.scene.BattleScene;
import com.li.battle.resource.TriggerConfig;
import com.li.battle.trigger.Trigger;
import lombok.Getter;

/**
 * 固定施法引爆型Trigger
 * @author li-yuanwen
 * @date 2022/5/27
 */
@Getter
public class FixCasterDetonateTrigger extends Trigger {

    /** 当前叠加次数 **/
    private int curNum;

    public FixCasterDetonateTrigger(long unitId, long parent, int skillId, int buffId, TriggerConfig config, BattleScene scene) {
        super(unitId, parent, skillId, buffId, config, scene);
    }

    public int increment() {
        return ++curNum;
    }

    public void reset() {
        this.curNum = 0;
    }
}

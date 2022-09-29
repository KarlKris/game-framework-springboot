package com.li.battle.trigger.creator;

import com.li.battle.core.scene.BattleScene;
import com.li.battle.resource.TriggerConfig;
import com.li.battle.trigger.*;
import com.li.battle.trigger.core.TargetDetonateTrigger;
import org.springframework.stereotype.Component;

/**
 * 某一目标引爆触发器创建器
 * @author li-yuanwen
 * @date 2022/9/26
 */
@Component
public class TargetDetonateTriggerCreator implements TriggerCreator {

    @Override
    public TriggerType[] getTypes() {
        return new TriggerType[] {
                TriggerType.TARGET_DETONATE
        };
    }

    @Override
    public Trigger newInstance(long unitId, long parent, int skillId, int buffId, TriggerConfig config, BattleScene scene) {
        return new TargetDetonateTrigger(unitId, parent, skillId, buffId, config, scene);
    }
}

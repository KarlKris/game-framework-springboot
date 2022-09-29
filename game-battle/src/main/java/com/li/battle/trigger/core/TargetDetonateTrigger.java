package com.li.battle.trigger.core;

import com.li.battle.core.scene.BattleScene;
import com.li.battle.resource.TriggerConfig;
import com.li.battle.trigger.Trigger;
import com.li.battle.trigger.domain.DetonateTriggerParam;

/**
 * 某一目标引爆触发器
 * @author li-yuanwen
 * @date 2022/9/26
 */
public class TargetDetonateTrigger extends Trigger  {

    /** 当前叠加的次数 **/
    private int curNum;
    /** 当前引爆的目标 **/
    private long targetUnitId;
    /** 上次添加的回合数 **/
    private long lastRound;
    /** 有效回合数 **/
    private final int validRound;

    public TargetDetonateTrigger(long unitId, long parent, int skillId, int buffId, TriggerConfig config, BattleScene scene) {
        super(unitId, parent, skillId, buffId, config, scene);
        int expire = ((DetonateTriggerParam) config.getParam()).getExpire();
        validRound = expire == 0 ? expire : expire / scene.getRoundPeriod();
    }

    public int increment(long target) {
        long curRound = getScene().getSceneRound();
        if (targetUnitId != target || isExpire(curRound)) {
            targetUnitId = target;
            curNum = 0;
        }
        curNum++;
        lastRound = curRound;
        return curNum;
    }

    public void reset() {
        targetUnitId = 0;
        lastRound = getScene().getSceneRound();
        curNum = 0;
    }

    private boolean isExpire(long curRound) {
        if (validRound == 0) {
            return false;
        }

        return curRound - lastRound > validRound;
    }

}

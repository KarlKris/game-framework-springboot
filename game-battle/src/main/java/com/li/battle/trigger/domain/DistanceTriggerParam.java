package com.li.battle.trigger.domain;

import com.li.battle.trigger.TriggerType;
import lombok.Getter;

/**
 * 双方间距触发器参数
 * @author li-yuanwen
 * @date 2022/9/26
 */
@Getter
public class DistanceTriggerParam implements TriggerParam {

    /** 双方是同阵营 **/
    private boolean sameCamp;
    /** 间距 **/
    private int distance;

    @Override
    public TriggerType getType() {
        return TriggerType.DISTANCE;
    }
}

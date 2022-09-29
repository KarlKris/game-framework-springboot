package com.li.battle.trigger.domain;

import com.li.battle.trigger.TriggerType;
import lombok.*;

/**
 * 引爆类触发器参数
 * @author li-yuanwen
 * @date 2022/9/26
 */
@Getter
public class DetonateTriggerParam implements TriggerParam {

    private TriggerType type;
    /** 监听技能id集 **/
    private int[] skillIds;
    /** 目标次数 **/
    private int num;
    /** 有效时长(毫秒 0=永久) **/
    private int expire;

    @Override
    public TriggerType getType() {
        return TriggerType.TARGET_DETONATE;
    }
}

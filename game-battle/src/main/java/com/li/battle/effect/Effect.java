package com.li.battle.effect;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * 效果
 * @author li-yuanwen
 * @date 2022/5/17
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type")
public interface Effect {

    /**
     * 技能效果类型
     * @return 技能效果类类型
     */
    String getType();

    /**
     * 效果触发
     */
    void onAction();

}

package com.li.battle.resource;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.li.battle.effect.domain.EffectParam;
import com.li.common.resource.anno.*;
import lombok.Getter;

import java.util.List;

/**
 * 一次性触发效果类技能配置
 * @author li-yuanwen
 * @date 2022/5/17
 */
@Getter
@ResourceObj
public class GeneralSkillConfig {

    /** 技能id **/
    @ResourceId
    private int id;
    /** 技能施法范围 **/
    private int range;
    /** 技能选择目标选择集 **/
    private List<Integer> selectorIds;
    /** 技能起手阶段效果 **/
    private EffectParam[] startEffects;
    /** 前摇时长(毫秒) **/
    private int frontRockingTime;
    /** 前摇阶段是否可打断 **/
    private boolean frontInterrupted;
    /** 施法阶段效果 **/
    private EffectParam[] spellEffects;
    /** 后摇时长(毫秒) **/
    private int backRockingTime;
    /** 后摇阶段是否可打断 **/
    private boolean backInterrupted;
    /** 结束阶段效果 **/
    private EffectParam[] finishEffects;

    @JsonIgnore
    public int getDurationTime() {
        return frontRockingTime + backRockingTime;
    }

}

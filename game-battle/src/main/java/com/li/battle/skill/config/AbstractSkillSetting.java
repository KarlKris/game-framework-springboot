package com.li.battle.skill.config;

import lombok.Getter;

/**
 * 主动技能配置表基类
 * @author li-yuanwen
 */
@Getter
public abstract class AbstractSkillSetting implements SkillSetting {

    /** 技能唯一标识 **/
    protected int id;

    /** 目标选择表#id **/
    protected int selectId;

    /** 技能释放前置条件表#id **/
    protected int conditionId;

    /** 技能起手阶段执行--效果表#id **/
    protected int startEffectId;

    /** 前摇时长(毫秒) **/
    protected int castPoint;

    /** 后摇时长(毫秒) **/
    protected int backPoint;

    /** 技能结束阶段--效果表#id **/
    protected int finishEffectId;

    /** 技能CD(毫秒) **/
    protected int cd;

    @Override
    public int getId() {
        return id;
    }
}

package com.li.battle.resource;

import lombok.Getter;

/**
 * 技能基础表
 * @author li-yuanwen
 * @date 2022/5/17
 */
@Getter
public class SkillConfig {

    /** 技能唯一标识 **/
    private int id;
    /** 技能类型 **/
    private byte type;
    /** 技能CD(毫秒) **/
    private int cooldown;
    /** 技能射程 **/
    private int range;


}

package com.li.battle.skill.model;

/**
 * 技能类型：采用标记位（可进行或操作）的形式来表明这些技能的类
 * @author li-yuanwen
 */
public enum SkillType {

	/** 被动类型 0000 0001 **/
	PASSIVE_SKILL((byte) (1)),

	/** 一次性触发效果型 0000 0010 **/
	GENERAL_SKILL((byte) (1 << 1)),

	/** 持续性触发效果型 0000 0100 **/
	CHANNEL_SKILL((byte) (1 << 2)),

	/** 开关类型 0000 1000 **/
	TOGGLE_SKILL((byte) (1 << 3)),

	/** 激活类型 0001 0000 **/
	ACTIVATE_SKILL((byte) (1 << 4)),

	;

	/** 技能类型标记位 **/
	private final byte type;

	SkillType(byte type) {
		this.type = type;
	}



	/** 判断技能类型归属 **/
	public static boolean belongTo(byte type, SkillType skillType) {
		return (type & skillType.type) != 0;
	}


}

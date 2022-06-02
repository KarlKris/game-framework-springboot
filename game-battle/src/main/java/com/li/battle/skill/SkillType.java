package com.li.battle.skill;

/**
 * 技能类型：采用标记位（可进行异或操作）的形式来表明这些技能的类
 *
 * @author li-yuanwen
 */
public enum SkillType {

	// 被动技能用末尾表示

	/** 被动类型 0000 000 1 **/
	PASSIVE_SKILL((byte) (1)),

	// 主动类技能 用低四位中前3位表示

	/** 一次性触发效果型 0000 001 0 **/
	GENERAL_SKILL((byte) (1 << 1)),

	/** 持续性触发效果型 0000 010 0 **/
	CHANNEL_SKILL((byte) (1 << 2)),

	/** 开关类型 0000 011 0 **/
	TOGGLE_SKILL((byte) (1 << 2 + 1 << 1)),

	/** 激活类型 0001 100 0 **/
	ACTIVATE_SKILL((byte) (1 << 3)),

	;

	/** 技能类型标记位 **/
	private final byte type;

	SkillType(byte type) {
		this.type = type;
	}



	/** 判断技能类型归属 **/
	public static boolean belongTo(byte type, SkillType skillType) {
		if (skillType == SkillType.PASSIVE_SKILL) {
			return (type & SkillType.PASSIVE_SKILL.type) == 1;
		}
		int b = type ^ skillType.type;
		return b == 0 || b == 1;
	}


}

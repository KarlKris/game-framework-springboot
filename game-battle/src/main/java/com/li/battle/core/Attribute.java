package com.li.battle.core;

/**
 * 战斗属性
 * @author li-yuanwen
 */
public enum Attribute {

    /** 血量 **/
    HP,

    /** 当前血量 **/
    CUR_HP,

    /** 血量上限值 **/
    HP_MAX,

    /** 物理攻击 **/
    PHYSICAL_ATTACK,

    /** 法术强度 **/
    MAGIC_STRENGTH,

    /** 移速 **/
    SPEED,

    /** 护甲 **/
    ARMOR,

    /** 魔抗 **/
    MAGIC_RESISTANCE,

    /** 穿甲 **/
    PENETRATES_ARMOR,

    /** 暴击率 **/
    CRIT,

    /** 暴击伤害倍率(百分比) **/
    CRITIC_DAMAGE(200),

    /** 增伤万分比 **/
    DAMAGE_INC,

    /** 减伤万分比 **/
    DAMAGE_DEC,

    ;


    /** 默认值 **/
    private final long defaultValue;

    Attribute() {
        this.defaultValue = 0;
    }

    Attribute(long defaultValue) {
        this.defaultValue = defaultValue;
    }

    public long getDefaultValue() {
        return defaultValue;
    }
}

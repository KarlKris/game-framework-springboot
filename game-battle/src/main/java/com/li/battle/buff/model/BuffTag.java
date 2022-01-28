package com.li.battle.buff.model;

/**
 * buff 标签
 * @author li-yuanwen
 */
public enum BuffTag {

    /** 金系 **/
    METAL((short) (1)),

    /** 木系 **/
    WOOD((short) (1 << 1)),

    /** 水系 **/
    WATER((short) (1 << 2)),

    /** 火系 **/
    FIRE((short) (1 << 3)),

    /** 土系 **/
    EARTH((short) (1 << 4)),

    ;

    /** 标记 **/
    private short tag;

    BuffTag(short tag) {
        this.tag = tag;
    }



}

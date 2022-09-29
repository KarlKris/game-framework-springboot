package com.li.battle.buff;

import lombok.Getter;

/**
 * buff 标签
 * @author li-yuanwen
 */
@Getter
public enum BuffTag {

    /** 金系 **/
    METAL(1),

    /** 木系 **/
    WOOD(1 << 1),

    /** 水系 **/
    WATER(1 << 2),

    /** 火系 **/
    FIRE(1 << 3),

    /** 土系 **/
    EARTH(1 << 4),

    ;

    /** 标记 **/
    private final int tag;

    BuffTag(int tag) {
        this.tag = tag;
    }



}

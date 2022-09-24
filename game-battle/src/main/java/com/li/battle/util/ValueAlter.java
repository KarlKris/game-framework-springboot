package com.li.battle.util;

/**
 * 数值变更持有类
 * @author li-yuanwen
 * @date 2022/5/24
 */
public class ValueAlter {

    /** 变更值 **/
    private final long value;

    /** 最终变更值 **/
    private long addValue;
    /** 能否减少最终值 **/
    private boolean canImpairValue;

    public ValueAlter(long value) {
        this(value, true);
    }

    public ValueAlter(long value, boolean canImpairValue) {
        this.value = value;
        this.canImpairValue = canImpairValue;
    }

    public void incrementValue(long value) {
        if (!canImpairValue) {
            return;
        }
        this.addValue += value;
    }

    public void cantImpairValue() {
        canImpairValue = false;
    }

    public long getValue() {
        if (addValue > 0) {
            return value + addValue;
        }
        return canImpairValue ? value + addValue : value;
    }

    public boolean isCanImpairValue() {
        return canImpairValue;
    }
}

package com.li.battle.buff;

import com.li.battle.buff.core.Buff;

import java.util.function.BiFunction;

/**
 * buff合并规则
 * @author li-yuanwen
 * @date 2022/5/19
 */
public enum BuffMerge {

    /** 增加Buff层数 **/
    INCREASE_BUFF((b1, b2) -> {
        b1.increaseLayer(b2.getLayer());
        return true;
    }),

    /** 增加buff等级 **/
    INCREASE_LEVEL((b1, b2) -> {
        b1.increaseLevel(b2.getLevel());
        return true;
    }),

    /** 增加buff持续时间 **/
    INCREASE_DURATION((b1, b2) -> {
        b1.increaseDuration(b2.getDuration());
        return true;
    }),

    /** 不可合并 **/
    CANT_MERGE((b1, b2) -> false)

    ;

    /** 现存同类型buff,新增buff **/
    private final BiFunction<Buff, Buff, Boolean> mergeFunction;

    BuffMerge(BiFunction<Buff, Buff, Boolean> mergeFunction) {
        this.mergeFunction = mergeFunction;
    }

    /**
     * buff合并
     * @param buff1 buff1
     * @param buff2 buff2
     */
    public boolean merge(Buff buff1, Buff buff2) {
        return mergeFunction.apply(buff1, buff2);
    }
}

package com.li.battle.buff;

import com.li.battle.buff.core.Buff;

import java.util.function.*;

/**
 * buff合并规则
 * @author li-yuanwen
 * @date 2022/5/19
 */
public enum BuffMerge {

    /** 增加Buff层数 **/
    INCREASE_BUFF((b1, b2) -> {
        b1.increaseLayer(b2.getLayer());
    }),

    /** 增加buff等级 **/
    INCREASE_LEVEL((b1, b2) -> {
        b1.increaseLevel(b2.getLevel());
    }),

    /** 增加buff持续时间 **/
    INCREASE_DURATION((b1, b2) -> {
        b1.increaseDuration(b2.getDuration());
    }),

    /** 不可合并 **/
    CANT_MERGE(null)

    ;

    /** 现存同类型buff,新增buff **/
    private final BiConsumer<Buff, Buff> mergeConsumer;

    BuffMerge(BiConsumer<Buff, Buff> mergeConsumer) {
        this.mergeConsumer = mergeConsumer;
    }

    /**
     * buff合并
     * @param buff1 buff1
     * @param buff2 buff2
     */
    public void merge(Buff buff1, Buff buff2) {
        mergeConsumer.accept(buff1, buff2);
    }

    /**
     * 能否合并
     * @return true 能
     */
    public boolean isMergeable() {
        return mergeConsumer != null;
    }
}

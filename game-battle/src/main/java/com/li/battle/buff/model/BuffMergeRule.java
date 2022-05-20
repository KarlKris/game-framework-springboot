package com.li.battle.buff.model;

import java.util.function.BiFunction;

/**
 * buff合并规则
 * @author li-yuanwen
 * @date 2022/5/19
 */
public enum BuffMergeRule {


    ;

    /** 现存同类型buff,新增buff,合并后的buff **/
    private final BiFunction<Object, Object, Object> mergeFunction;

    BuffMergeRule(BiFunction<Object, Object, Object> mergeFunction) {
        this.mergeFunction = mergeFunction;
    }

    /**
     * buff合并
     * @param buff1 buff1
     * @param buff2 buff2
     * @return 合并后的buff
     */
    public Object merge(Object buff1, Object buff2) {
        return mergeFunction.apply(buff1, buff2);
    }
}

package com.li.battle.ai.composite;

/**
 * 并行器执行策略
 * @author li-yuanwen
 * @date 2022/1/25
 */
public enum ParallelPolicy {

    /** 某个子行为一旦成功或失败，立即返回 **/
    REQUIRE_ONE,

    /** 所有子行为全成功或全失败才算成功或失败，否则正在运行 **/
    REQUIRE_ALL,

    ;

}

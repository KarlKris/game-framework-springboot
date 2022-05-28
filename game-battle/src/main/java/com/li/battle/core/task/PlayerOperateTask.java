package com.li.battle.core.task;

/**
 * 玩家操作战斗相关操作任务
 * @author li-yuanwen
 * @date 2021/10/18
 */
public interface PlayerOperateTask<R> {

    /**
     * 执行玩家任务
     * @return 玩家任务执行结果
     */
    R run();
}

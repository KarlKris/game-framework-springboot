package com.li.battle.trigger.core;

/**
 * 触发器成功触发后的回调
 * @author li-yuanwen
 * @date 2022/5/27
 */
public interface TriggerSuccessCallback {


    /**
     * 成功触发后回调
     * @param detonateOwnerId 触发目标标识
     */
    void callback(long detonateOwnerId);


}

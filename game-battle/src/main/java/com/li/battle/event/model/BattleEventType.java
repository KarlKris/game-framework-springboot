package com.li.battle.event.model;

import com.li.battle.buff.monitor.BuffMonitor;

/**
 * 战斗事件类型
 * @author li-yuanwen
 * @date 2021/10/18
 */
public enum BattleEventType {


    ;

    /** 事件相关Buff监听器数组 **/
    private BuffMonitor[] monitors;

    BattleEventType(BuffMonitor... monitors) {
        this.monitors = monitors;
    }

}

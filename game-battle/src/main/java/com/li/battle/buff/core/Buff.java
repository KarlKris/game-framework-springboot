package com.li.battle.buff.core;

/**
 * buff基本接口
 * @author li-yuanwen
 */
public interface Buff extends BuffMonitor {

    /** 当Buff添加时，存在相同类型且施加者相同的时候，Buff执行刷新流程(更新Buff层数，等级，持续时间等数据) **/
    void onBuffRefresh();

    /** Buff实例化，加入容器后生效 **/
    void onBuffStart();

    /** Buff触发间隔效果 **/
    void onIntervalThink();

    /** 当Buff销毁前(还未从Buff容器中移除) **/
    void onBuffRemove();

    /** 当Buff销毁后(已从Buff容器中移除) **/
    void onBuffDestroy();

    /** 手动使buff失效 **/
    void expire();

    /**
     * 获取下一次触发间隔效果的回合数
     * @return 下一次触发间隔效果的回合数
     */
    long getNextRound();

    /**
     * 判断buff是否失效
     * @param curRound 当前场景回合数
     * @return true buff已失效
     */
    boolean expire(long curRound);
}

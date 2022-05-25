package com.li.battle.buff.core;

import com.li.battle.core.context.AbstractContext;
import com.li.battle.event.EventReceiver;

/**
 * buff基本接口
 * @author li-yuanwen
 */
public interface Buff extends EventReceiver {

    /**
     * 获取buff标识
     * @return buff标识
     */
    int getBuffId();

    /**
     * 获取buff关联的技能id
     * @return buff关联的技能id
     */
    int getSkillId();

    /**
     * 获取buff施法对象标识
     * @return 取buff施法对象标识
     */
    long getCaster();

    /**
     * 获取buff挂载目标标识
     * @return buff挂载目标标识
     */
    long getParent();

    /**
     * 当Buff添加时，存在相同类型且施加者相同的时候，Buff执行刷新流程(更新Buff层数，等级，持续时间等数据)
     * @param other 其他buff
     **/
    void onBuffRefresh(Buff other);

    /**
     * 获取buff层数
     * @return buff层数
     */
    int getLayer();

    /**
     * 增加buff层数
     * @param addLayer 增值
     */
    void increaseLayer(int addLayer);

    /**
     * 获取buff等级
     * @return buff等级
     */
    int getLevel();

    /**
     * 增加buff等级
     * @param addLevel 增值
     */
    void increaseLevel(int addLevel);

    /**
     * 增加buff持续时间
     * @param duration 持续时间
     */
    void increaseDuration(int duration);

    /**
     * 获取持续时间
     * @return 持续时间
     */
    int getDuration();

    /**
     * 手动使buff失效
     **/
    void expire();

    /**
     * 判断buff是否已被手动失效
     * @return true buff已被手动失效
     */
    boolean isManualExpire();

    /**
     * 获取下一次触发间隔效果的回合数
     * @return 下一次触发间隔效果的回合数
     */
    long getNextRound();

    /**
     * 更新下一次触发效果的回合数
     * @param nextRound 下一次触发间隔效果的回合数
     */
    void updateNextRound(long nextRound);

    /**
     * 判断buff是否失效
     * @param curRound 当前场景回合数
     * @return true buff已失效
     */
    boolean expire(long curRound);

    /**
     * 获取buff创建上下文
     * @return buff创建上下文
     */
    AbstractContext getContext();


}

package com.li.battle.buff.core;

import com.li.battle.buff.BuffContext;
import com.li.battle.core.scene.BattleScene;
import com.li.battle.event.EventReceiver;
import com.li.battle.resource.BuffConfig;

/**
 * buff基本接口
 * @author li-yuanwen
 */
public interface Buff extends EventReceiver {

    /**
     * buff唯一标识
     * @return 唯一标识
     */
    long getId();

    /**
     * 获取buff标识
     * @return buff标识
     */
    int getBuffId();

    /**
     * 获取Buff配置
     * @return Buff配置
     */
    BuffConfig getConfig();

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
     * 标识buff失效,用于在buff加入容器前,被其他buff驱散
     */
    void markExpire();

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
     * 获取失效回合数
     * @return 失效回合
     */
    long getExpireRound();

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
     * 关联的战斗场景
     * @return 战斗场景
     */
    BattleScene battleScene();

    /**
     * Buff创建时候的一些上下文数据，它是一个不确定的项
     * 通过外部传入各种自定义的数据，然后在Buff逻辑中使用这些自定义数据
     * @return 上下文数据
     */
    BuffContext buffContext();


}

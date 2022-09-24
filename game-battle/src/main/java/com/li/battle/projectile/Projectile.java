package com.li.battle.projectile;

import com.li.battle.core.scene.BattleScene;
import com.li.battle.event.EventReceiver;

/**
 * 子弹抽象接口--子弹本身是没有任何特殊逻辑的，它只有位置更新，检查命中目标和是否结束并销毁这几个简单的功能
 * @author li-yuanwen
 */
public interface Projectile extends EventReceiver {


    /**
     * 子弹id
     * @return 子弹id
     */
    int getProjectileId();

    /**
     * 位置更新
     */
    void updatePosition();

    /**
     * 检查命中目标并在命中后执行命中逻辑
     */
    void tryHit();

    /**
     * 检查是否结束
     * @return true 结束
     */
    boolean checkFinish();


    /**
     * 子弹所在的场景
     * @return 场景
     */
    BattleScene battleScene();


    /**
     * 获取关联的技能id
     * @return 技能id
     */
    int getSkillId();



}

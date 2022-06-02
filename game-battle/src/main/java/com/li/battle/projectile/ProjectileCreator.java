package com.li.battle.projectile;

import com.li.battle.resource.ProjectileConfig;
import com.li.battle.skill.BattleSkill;

/**
 * 子弹创建
 * @author li-yuanwen
 * @date 2022/6/2
 */
public interface ProjectileCreator {


    /**
     * 负责的子弹类型
     * @return 子弹类型
     */
    ProjectileType getType();


    /**
     * 创建子弹
     * @param skill 技能
     * @param config 子弹配置
     * @return 子弹
     */
    Projectile newInstance(BattleSkill skill, ProjectileConfig config);

}

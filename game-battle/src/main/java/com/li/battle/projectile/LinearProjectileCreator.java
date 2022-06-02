package com.li.battle.projectile;

import com.li.battle.resource.ProjectileConfig;
import com.li.battle.skill.BattleSkill;
import org.springframework.stereotype.Component;

/**
 * 线性子弹创建器
 * @author li-yuanwen
 * @date 2022/6/2
 */
@Component
public class LinearProjectileCreator implements ProjectileCreator {

    @Override
    public ProjectileType getType() {
        return ProjectileType.LINEAR_PROJECTILE;
    }

    @Override
    public Projectile newInstance(BattleSkill skill, ProjectileConfig config) {
        return null;
    }
}

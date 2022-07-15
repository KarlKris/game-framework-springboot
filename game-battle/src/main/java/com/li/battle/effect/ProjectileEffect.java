package com.li.battle.effect;

import com.li.battle.buff.core.Buff;
import com.li.battle.core.BattleSceneHelper;
import com.li.battle.core.scene.BattleScene;
import com.li.battle.projectile.Projectile;
import com.li.battle.resource.ProjectileConfig;
import com.li.battle.skill.BattleSkill;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * 添加子弹效果
 * @author li-yuanwen
 * @date 2022/6/6
 */
@Slf4j
@Getter
public class ProjectileEffect extends EffectAdapter<Buff> {

    /** 子弹配置标识 **/
    private int projectileId;

    @Override
    public void onAction(BattleSkill skill) {
        BattleScene scene = skill.getContext().getScene();

        BattleSceneHelper battleSceneHelper = scene.battleSceneHelper();
        // 子弹配置
        ProjectileConfig projectileConfig = battleSceneHelper.configHelper().getProjectileConfigById(projectileId);

        // 创建子弹
        Projectile projectile = battleSceneHelper.projectileCreatorHolder()
                .getProjectileCreator(projectileConfig.getType())
                .newInstance(skill, projectileConfig);

        if (projectile != null) {
            scene.projectileManager().addProjectile(projectile);
        }
    }

}

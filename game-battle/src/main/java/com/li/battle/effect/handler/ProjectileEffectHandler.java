package com.li.battle.effect.handler;

import com.li.battle.core.BattleSceneHelper;
import com.li.battle.core.scene.BattleScene;
import com.li.battle.effect.EffectType;
import com.li.battle.effect.domain.ProjectileEffectParam;
import com.li.battle.effect.source.*;
import com.li.battle.projectile.Projectile;
import com.li.battle.resource.ProjectileConfig;
import org.springframework.stereotype.Component;

/**
 * 创建子弹效果(子弹只能由buff来创建)
 * @author li-yuanwen
 * @date 2022/9/23
 */
@Component
public class ProjectileEffectHandler extends AbstractEffectHandler<BuffEffectSource, ProjectileEffectParam> {

    @Override
    public EffectType getType() {
        return EffectType.PROJECTILE;
    }

    @Override
    protected void execute0(BuffEffectSource source, ProjectileEffectParam effectParam) {
        BattleScene scene = source.battleScene();

        BattleSceneHelper battleSceneHelper = scene.battleSceneHelper();
        // 子弹配置
        ProjectileConfig projectileConfig = battleSceneHelper.configHelper().getProjectileConfigById(effectParam.getProjectileId());

        // 创建子弹
        Projectile projectile = battleSceneHelper.projectileCreatorHolder()
                .getProjectileCreator(projectileConfig.getType())
                .newInstance(source, projectileConfig);

        if (projectile != null) {
            scene.projectileManager().addProjectile(projectile);
        }
    }
}

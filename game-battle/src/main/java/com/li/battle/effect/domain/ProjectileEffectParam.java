package com.li.battle.effect.domain;

import com.li.battle.effect.*;
import lombok.Getter;

/**
 * 创建子弹效果参数
 * @author li-yuanwen
 * @date 2022/9/23
 */
@Getter
public class ProjectileEffectParam implements EffectParam {

    /** 子弹配置标识 **/
    private int projectileId;

    @Override
    public EffectType type() {
        return EffectType.PROJECTILE;
    }
}

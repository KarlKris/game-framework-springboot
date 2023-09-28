package com.li.battle.effect.domain;

import com.li.battle.effect.*;
import lombok.Getter;
import lombok.ToString;

/**
 * 创建子弹效果参数
 * @author li-yuanwen
 * @date 2022/9/23
 */
@Getter
@ToString
public class ProjectileEffectParam implements EffectParam {

    /** 子弹配置标识 **/
    private int projectileId;

    @Override
    public EffectType getType() {
        return EffectType.PROJECTILE;
    }
}

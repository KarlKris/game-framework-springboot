package com.li.battle.resource;

import com.li.battle.projectile.ProjectileType;
import com.li.common.resource.anno.ResourceId;
import com.li.common.resource.anno.ResourceObj;
import lombok.Getter;

/**
 * 子弹配置
 * @author li-yuanwen
 * @date 2022/6/2
 */
@Getter
@ResourceObj
public class ProjectileConfig {

    /** 子弹id **/
    @ResourceId
    private int id;
    /** 子弹类型 **/
    private ProjectileType type;
    /** 飞行速度 **/
    private int speed;
    /** 飞行距离,在type==LINEAR_PROJECTILE时有效 **/
    private int range;
    /** 子弹是否可拦截 **/
    private boolean intercept;
    /** 子弹形状一律用矩形  长度 **/
    private int length;
    /** 子弹形状一律用矩形  宽度 **/
    private int width;


}

package com.li.battle.resource;

import com.li.battle.selector.SelectorType;
import com.li.common.resource.anno.ResourceId;
import com.li.common.resource.anno.ResourceObj;
import lombok.Getter;

/**
 * 选择器配置
 * @author li-yuanwen
 * @date 2022/5/25
 */
@Getter
@ResourceObj
public class SelectorConfig {

    /** 选择器标识 **/
    @ResourceId
    private int id;
    /** 选择器类型 **/
    private SelectorType type;
    /** 选择范围 **/
    private int range;
    /** 选择宽度 **/
    private int width;

}

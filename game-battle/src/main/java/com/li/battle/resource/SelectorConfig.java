package com.li.battle.resource;

import com.li.battle.selector.SelectorType;
import lombok.Getter;

/**
 * 选择器配置 选择器选择需要的参数 选择人,选择器配置,射程
 * @author li-yuanwen
 * @date 2022/5/25
 */
@Getter
public class SelectorConfig {

    /** 选择器标识 **/
    private int id;
    /** 选择器类型 **/
    private SelectorType type;
    /**  **/

}

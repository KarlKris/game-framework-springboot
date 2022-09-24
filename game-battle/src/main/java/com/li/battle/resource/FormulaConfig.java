package com.li.battle.resource;

import com.li.common.resource.anno.*;
import lombok.Getter;

/**
 * 公式配置
 * @author li-yuanwen
 * @date 2022/9/23
 */
@Getter
@ResourceObj
public class FormulaConfig {

    /** 唯一标识 **/
    @ResourceId
    private String id;
    /** 公式内容 **/
    private String content;

}

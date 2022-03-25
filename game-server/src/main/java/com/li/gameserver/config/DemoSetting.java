package com.li.gameserver.config;

import com.li.gamecommon.resource.anno.ResourceId;
import com.li.gamecommon.resource.anno.ResourceObj;
import com.li.gamecommon.resource.storage.ResourceValidate;

import java.util.List;
import java.util.Map;

/**
 * @author li-yuanwen
 * @date 2022/3/25
 */
@ResourceObj
public class DemoSetting implements ResourceValidate {


    /** 主键标识 **/
    @ResourceId
    private int id;
    /** 数值 **/
    private int num;
    /** 字符串 **/
    private String str;
    /** 集合 **/
    private List<Integer> list;
    /** 字典 **/
    private Map<Integer, Integer> map;


}

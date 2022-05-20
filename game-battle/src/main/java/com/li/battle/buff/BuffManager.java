package com.li.battle.buff;

import com.li.battle.buff.core.Buff;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Buff管理
 * @author li-yuanwen
 * @date 2022/5/18
 */
public class BuffManager {

    /** 场景内所有的buff **/
    private final Map<Integer, List<Buff>> buffs = new HashMap<>();

}

package com.li.core.cache.enhance;

import com.li.core.dao.AbstractEntity;

import java.io.Serializable;

/**
 * 被增强实体接口
 * @author li-yuanwen
 * @date 2022/3/15
 */
public interface EnhanceEntity<PK extends Comparable<PK> & Serializable> {


    /**
     * 获取被增强的实体
     * @return 被增强的实体
     */
    AbstractEntity<PK> getEntity();


}

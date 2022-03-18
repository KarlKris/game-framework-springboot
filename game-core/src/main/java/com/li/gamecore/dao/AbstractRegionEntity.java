package com.li.gamecore.dao;

import java.io.Serializable;

/**
 * 区域实体基类
 * @author li-yuanwen
 * @date 2022/3/8
 */
public abstract class AbstractRegionEntity<PK extends Comparable<PK> & Serializable
        , FK extends Comparable<FK> & Serializable> extends AbstractEntity<PK> {

    /** 外键 **/
    private FK owner;

    public AbstractRegionEntity() {
        super();
    }

    public AbstractRegionEntity(PK id, FK owner) {
        super(id);
        this.owner = owner;
    }


    public FK getOwner() {
        return owner;
    }

}

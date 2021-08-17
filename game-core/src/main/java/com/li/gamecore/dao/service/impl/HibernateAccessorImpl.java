package com.li.gamecore.dao.service.impl;

import com.li.gamecore.dao.IEntity;
import com.li.gamecore.dao.service.Accessor;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;
import org.springframework.stereotype.Repository;

import java.io.Serializable;

/**
 * @author li-yuanwen
 * 基于Hibernate的数据库访问接口实现
 */
@Repository
public class HibernateAccessorImpl extends HibernateDaoSupport implements Accessor {


    @Override
    public <PK extends Comparable<PK> & Serializable, T extends IEntity<PK>> T load(PK id, Class<T> tClass) {
        return getHibernateTemplate().load(tClass, id);
    }

    @Override
    public <PK extends Comparable<PK> & Serializable, T extends IEntity<PK>> void remove(T entity) {
        getHibernateTemplate().delete(entity);
    }

    @Override
    public <PK extends Comparable<PK> & Serializable, T extends IEntity<PK>> void update(T entity) {
        getHibernateTemplate().update(entity);
    }

    @Override
    public <PK extends Comparable<PK> & Serializable, T extends IEntity<PK>> void create(T entity) {
        getHibernateTemplate().save(entity);
    }
}

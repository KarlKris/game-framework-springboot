package com.li.gamecore.dao.core.impl;

import com.li.gamecore.dao.IEntity;
import com.li.gamecore.dao.core.DataBaseAccessor;
import org.hibernate.SessionFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.Serializable;

/**
 * @author li-yuanwen
 * 基于Hibernate的数据库访问接口实现
 */
@Component
@ConditionalOnBean(SessionFactory.class)
public class HibernateAccessorImpl extends HibernateDaoSupport implements DataBaseAccessor {


    @Override
    @Resource
    protected HibernateTemplate createHibernateTemplate(SessionFactory sessionFactory) {
        return super.createHibernateTemplate(sessionFactory);
    }

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

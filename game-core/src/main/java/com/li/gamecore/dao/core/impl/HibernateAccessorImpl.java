package com.li.gamecore.dao.core.impl;

import com.li.gamecore.dao.IEntity;
import com.li.gamecore.dao.core.DataBaseAccessor;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import javax.persistence.EntityManagerFactory;
import java.io.Serializable;

/**
 * @author li-yuanwen
 * 基于Hibernate的数据库访问接口实现
 */
@Repository
@ConditionalOnProperty(value = "spring.datasource.url")
public class HibernateAccessorImpl extends HibernateDaoSupport implements DataBaseAccessor {


    /** 注入SessionFactory **/
    @Resource
    private void setSessionFactory(@Qualifier("entityManagerFactory") EntityManagerFactory emf) {
        super.setSessionFactory(emf.unwrap(SessionFactory.class));
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

package com.li.gamecore.dao.core.impl;

import com.li.gamecore.dao.IEntity;
import com.li.gamecore.dao.core.DataBaseAccessor;
import com.li.gamecore.dao.core.DataBaseQuerier;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.orm.hibernate5.HibernateCallback;
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.persistence.EntityManagerFactory;
import java.io.Serializable;
import java.util.List;

/**
 * @author li-yuanwen
 * 基于Hibernate的数据库访问接口实现
 */
@Repository
@ConditionalOnProperty(value = "spring.datasource.url")
@Transactional
public class HibernateAccessorImpl extends HibernateDaoSupport implements DataBaseAccessor, DataBaseQuerier {

    // ----------------- HibernateDaoSupport 接口相关--------------------------------------------

    /** 注入SessionFactory **/
    @Resource
    private void setSessionFactory(@Qualifier("entityManagerFactory") EntityManagerFactory emf) {
        super.setSessionFactory(emf.unwrap(SessionFactory.class));
    }

    // ----------------- DataBaseAccessor 接口相关------------------------------------------------

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

    // ----------------- DataBaseAccessor 接口相关------------------------------------------------

    @Override
    public <E> List<E> all(Class<E> entityClass) {
        HibernateTemplate hibernateTemplate = getHibernateTemplate();
        return hibernateTemplate.loadAll(entityClass);
    }

    @Override
    public <E, T> List<T> query(Class<E> entity, Class<T> returnClass, String queryName, Object... params) {
        HibernateTemplate hibernateTemplate = getHibernateTemplate();
        return hibernateTemplate.execute((HibernateCallback<List<T>>) session -> {
            Query query = session.getNamedQuery(queryName);
            if (params != null) {
                int length = params.length;
                for (int i = 0; i < length; i++) {
                    query.setParameter(i, params[i]);
                }
            }

            return query.getResultList();
        });
    }

    @Override
    public <E, T> T uniqueQuery(Class<E> entityClass, Class<T> returnClass, String queryName, Object... params) {
        HibernateTemplate hibernateTemplate = getHibernateTemplate();
        return hibernateTemplate.execute(session -> {
            Query query = session.getNamedQuery(queryName);
            if (params != null) {
                int length = params.length;
                for (int i = 0; i < length; i++) {
                    query.setParameter(i, params[i]);
                }
            }

            return (T) query.getSingleResult();
        });
    }
}

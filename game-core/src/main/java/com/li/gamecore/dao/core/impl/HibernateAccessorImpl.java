package com.li.gamecore.dao.core.impl;

import com.li.gamecore.dao.IEntity;
import com.li.gamecore.dao.core.DataBaseAccessor;
import com.li.gamecore.dao.core.DataBaseQuerier;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.orm.hibernate5.HibernateCallback;
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.List;

/**
 * @author li-yuanwen
 * 基于Hibernate的数据库访问接口实现
 */
@Repository
@ConditionalOnProperty(value = "spring.datasource.url")
public class HibernateAccessorImpl implements DataBaseAccessor, DataBaseQuerier {



    @Autowired
    private HibernateTemplate hibernateTemplate;


    // ----------------- DataBaseAccessor 接口相关------------------------------------------------


    @Override
    @Transactional(readOnly = true)
    public <PK extends Comparable<PK> & Serializable, T extends IEntity<PK>> T load(PK id, Class<T> tClass) {
        return hibernateTemplate.load(tClass, id);
    }

    @Override
    @Transactional
    public <PK extends Comparable<PK> & Serializable, T extends IEntity<PK>> void remove(T entity) {
        hibernateTemplate.delete(entity);
    }

    @Override
    @Transactional
    public <PK extends Comparable<PK> & Serializable, T extends IEntity<PK>> void update(T entity) {
        hibernateTemplate.update(entity);
    }

    @Override
    @Transactional
    public <PK extends Comparable<PK> & Serializable, T extends IEntity<PK>> void create(T entity) {
        hibernateTemplate.save(entity);
    }

    // ----------------- DataBaseAccessor 接口相关------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public <E> List<E> all(Class<E> entityClass) {
        return hibernateTemplate.loadAll(entityClass);
    }

    @Override
    @Transactional(readOnly = true)
    public <E, T> List<T> query(Class<E> entity, Class<T> returnClass, String queryName, Object... params) {
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
    @Transactional(readOnly = true)
    public <E, T> T uniqueQuery(Class<E> entityClass, Class<T> returnClass, String queryName, Object... params) {
        return hibernateTemplate.execute(session -> {
            Query query = session.getNamedQuery(queryName);
            if (params != null) {
                int length = params.length;
                for (int i = 0; i < length; i++) {
                    query.setParameter(i, params[i]);
                }
            }

            List list = query.getResultList();
            if (CollectionUtils.isEmpty(list)) {
                return null;
            }
            return (T) list.get(0);
        });
    }
}

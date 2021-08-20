package com.li.gamecore.dao.service.impl;

import com.li.gamecore.cache.anno.Cachedable;
import com.li.gamecore.dao.EntityBuilder;
import com.li.gamecore.dao.IEntity;
import com.li.gamecore.dao.core.DataBaseAccessor;
import com.li.gamecore.dao.javassist.JavassistProxyFactory;
import com.li.gamecore.dao.service.EntityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;

import java.io.Serializable;

/**
 * @author li-yuanwen
 */
@Service
@Slf4j
@ConditionalOnBean(DataBaseAccessor.class)
public class EntityServiceImpl implements EntityService {

    @Autowired
    private DataBaseAccessor dataBaseAccessor;
    @Autowired
    private JavassistProxyFactory javassistProxyFactory;

    @Override
    @Cachedable(name = "#{tClass.getName()}", key = "#{id}")
    public <PK extends Comparable<PK> & Serializable, T extends IEntity<PK>> T load(PK id, Class<T> tClass) {
        T t = dataBaseAccessor.load(id, tClass);
        return javassistProxyFactory.transform(t);
    }

    @Override
    @Cachedable(name = "#{tClass.getName()}", key = "#{id}")
    public <PK extends Comparable<PK> & Serializable, T extends IEntity<PK>> T loadOrCreate(PK id, Class<T> tClass
            , EntityBuilder<PK, T> entityBuilder) {
        T t = dataBaseAccessor.load(id, tClass);
        if (t == null) {
            t = entityBuilder.build(id);
            dataBaseAccessor.create(entityBuilder.build(id));
        }
        return javassistProxyFactory.transform(t);
    }

    @Override
    public <PK extends Comparable<PK> & Serializable, T extends IEntity<PK>> T create(T entity) {
        dataBaseAccessor.create(entity);
        return entity;
    }
}

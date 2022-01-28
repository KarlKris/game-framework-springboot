package com.li.gamecore.dao.service.impl;

import com.li.gamecore.cache.anno.CachedPut;
import com.li.gamecore.cache.anno.CachedRemove;
import com.li.gamecore.cache.anno.Cachedable;
import com.li.gamecore.dao.AbstractEntity;
import com.li.gamecore.dao.EntityBuilder;
import com.li.gamecore.dao.core.DataAccessor;
import com.li.gamecore.dao.javassist.JavassistProxyFactory;
import com.li.gamecore.dao.service.EntityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.Serializable;

/**
 * @author li-yuanwen
 */
@Service
@Slf4j
@ConditionalOnBean(DataAccessor.class)
public class EntityServiceImpl implements EntityService {

    @Resource
    private DataAccessor dataAccessor;
    @Resource
    private JavassistProxyFactory javassistProxyFactory;

    @Override
    @Cachedable(name = "#tClass.getName()", key = "#id")
    public <PK extends Comparable<PK> & Serializable, T extends AbstractEntity<PK>> T load(PK id, Class<T> tClass) {
        T t = dataAccessor.load(id, tClass);
        return javassistProxyFactory.transform(t);
    }

    @Override
    @Cachedable(name = "#tClass.getName()", key = "#id")
    public <PK extends Comparable<PK> & Serializable, T extends AbstractEntity<PK>> T loadOrCreate(PK id, Class<T> tClass
            , EntityBuilder<PK, T> entityBuilder) {
        T t = dataAccessor.load(id, tClass);
        if (t == null) {
            t = entityBuilder.build(id);
            dataAccessor.create(entityBuilder.build(id));
        }
        return javassistProxyFactory.transform(t);
    }

    @Override
    @CachedPut(name = "#entity.getClass().getName()", key = "#entity.getId()")
    public <PK extends Comparable<PK> & Serializable, T extends AbstractEntity<PK>> T create(T entity) {
        dataAccessor.create(entity);
        return javassistProxyFactory.transform(entity);
    }

    @Override
    @CachedRemove(name = "#entity.getClass().getName()", key = "#entity.getId()")
    public <PK extends Comparable<PK> & Serializable, T extends AbstractEntity<PK>> void remove(T entity) {
        dataAccessor.remove(entity);
    }
}

package com.li.gamecore.dao.service.impl;

import com.li.gamecore.dao.AbstractEntity;
import com.li.gamecore.dao.EntityBuilder;
import com.li.gamecore.dao.core.IDataAccessor;
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
@ConditionalOnBean(IDataAccessor.class)
public class EntityServiceImpl implements EntityService {

    @Resource
    private IDataAccessor IDataAccessor;
    @Resource
    private JavassistProxyFactory javassistProxyFactory;

    @Override
    public <PK extends Comparable<PK> & Serializable, T extends AbstractEntity<PK>> T load(PK id, Class<T> tClass) {
        T t = IDataAccessor.load(id, tClass);
        return javassistProxyFactory.transform(t);
    }

    @Override
    public <PK extends Comparable<PK> & Serializable, T extends AbstractEntity<PK>> T loadOrCreate(PK id, Class<T> tClass
            , EntityBuilder<PK, T> entityBuilder) {
        T t = IDataAccessor.load(id, tClass);
        if (t == null) {
            t = entityBuilder.build(id);
            IDataAccessor.create(t);
        }
        return javassistProxyFactory.transform(t);
    }

    @Override
    public <PK extends Comparable<PK> & Serializable, T extends AbstractEntity<PK>> T create(T entity) {
        IDataAccessor.create(entity);
        return javassistProxyFactory.transform(entity);
    }

    @Override
    public <PK extends Comparable<PK> & Serializable, T extends AbstractEntity<PK>> void remove(T entity) {
        IDataAccessor.remove(entity);
    }
}

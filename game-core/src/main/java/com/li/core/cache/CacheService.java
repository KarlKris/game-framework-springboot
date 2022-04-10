package com.li.core.cache;

import com.li.core.cache.config.CachedType;
import com.li.core.cache.core.cache.Cache;
import com.li.core.cache.core.manager.CacheManager;
import com.li.core.cache.enhance.EnhanceEntity;
import com.li.core.cache.enhance.Enhancer;
import com.li.core.dao.AbstractEntity;
import com.li.core.dao.AbstractRegionEntity;
import com.li.core.dao.EntityBuilder;
import com.li.core.dao.core.DataFinder;
import com.li.core.dao.core.IDataAccessor;
import com.li.core.dao.service.IDataPersistence;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author li-yuanwen
 * @date 2022/3/8
 */
@Slf4j
@Service
@ConditionalOnBean(IDataAccessor.class)
public class CacheService implements EntityCacheService, RegionEntityCacheService {


    private final IDataAccessor dataAccessor;
    private final IDataPersistence dataPersistence;
    private final DataFinder dataFinder;
    private final CacheManager cacheManager;
    private final Enhancer enhancer;

    public CacheService(@Autowired IDataAccessor dataAccessor
            , @Autowired  IDataPersistence dataPersistence
            , @Autowired  DataFinder dataFinder
            , @Autowired  CacheManager cacheManager
            , @Autowired  Enhancer enhancer) {
        this.dataAccessor = dataAccessor;
        this.dataPersistence = dataPersistence;
        this.dataFinder = dataFinder;
        this.cacheManager = cacheManager;
        this.enhancer = enhancer;
    }

    // --------------------- EntityCacheService 实现 ----------------------------------

    @Override
    public <PK extends Comparable<PK> & Serializable, T extends AbstractEntity<PK>> T loadEntity(PK id, Class<T> tClass) {
        T entity = dataPersistence.findById(id, tClass);
        if (entity == null) {
            entity = dataAccessor.load(id, tClass);
        } else if (entity.isDeleteStatus()) {
            entity = null;
        }
        return entity == null ? null : wrapIfNecessary(entity);
    }

    @Override
    public <PK extends Comparable<PK> & Serializable, T extends AbstractEntity<PK>> T loadOrCreate(PK id, Class<T> tClass, EntityBuilder<PK, T> entityBuilder) {
        T entity = loadEntity(id, tClass);
        if (entity == null) {
            T newInstance = entityBuilder.build(id);
            dataPersistence.commit(newInstance);
            entity = newInstance;
            return wrapIfNecessary(entity);
        }
        return entity;
    }

    @Override
    public <PK extends Comparable<PK> & Serializable, T extends AbstractEntity<PK>> T createEntity(T entity) {
        T originEntity = unwrapIfNecessary(entity);
        dataPersistence.commit(originEntity);
        return wrapIfNecessary(entity);
    }

    @Override
    public <PK extends Comparable<PK> & Serializable, T extends AbstractEntity<PK>> void remove(T entity) {
        T originEntity = unwrapIfNecessary(entity);
        originEntity.setDeleteStatus();
        dataPersistence.commit(originEntity);
    }


    // --------------------- RegionEntityCacheService 实现 ----------------------------------

    /** 区域实体HQL todo 考虑这种情况@Entity("tableName") */
    private static final String ALL_BY_OWNER_HQL = "select e.id from {0} as e where e.owner = {1}";

    @Override
    public <PK extends Comparable<PK> & Serializable
            , FK extends Comparable<FK> & Serializable
            , T extends AbstractRegionEntity<PK, FK>
            , R extends RegionEntityContext<PK, FK, T>> R loadRegionContext(FK owner, Class<T> tClass, RegionEntityContextBuilder builder) {
        // 先查询待持久化数据集
        Map<PK, T> map = dataPersistence.findAllByClass(tClass);
        // 再查询数据库
        List<T> queryResult = dataFinder.query(MessageFormat.format(ALL_BY_OWNER_HQL, tClass.getSimpleName(), owner), tClass);
        List<T> list = new ArrayList<>(queryResult.size());
        for (T t : queryResult) {
            // 获取持久化列表中的数据并替换
            T entity = map.remove(t.getId());
            // 数据待删除
            if (entity != null && entity.isDeleteStatus()) {
                continue;
            }
            // 增强
            list.add(wrapIfNecessary(entity != null ? entity : t));
        }

        if (!map.isEmpty()) {
            map.values().stream()
                    .filter(t -> !t.isDeleteStatus() && t.getOwner() == owner)
                    .map(this::wrapIfNecessary)
                    .forEach(list::add);
        }

        return builder.build(list);
    }


    @Override
    public <PK extends Comparable<PK> & Serializable
            , FK extends Comparable<FK> & Serializable
            , T extends AbstractRegionEntity<PK, FK>> void createRegionEntity(T entity) {
        T originEntity = unwrapIfNecessary(entity);
        dataPersistence.commit(originEntity);
        addOrRemoveEntity(originEntity, true);
    }

    @Override
    public <PK extends Comparable<PK> & Serializable
            , FK extends Comparable<FK> & Serializable
            , T extends AbstractRegionEntity<PK, FK>> void remove(T entity) {
        AbstractEntity<PK> originEntity = unwrapIfNecessary(entity);
        originEntity.setDeleteStatus();
        addOrRemoveEntity(entity, false);
    }

    /**
     * 将实体添加至缓存中或从移除中移除
     * @param entity 实体数据
     * @param add true 添加至缓存中
     * @param <PK> 主键类型
     * @param <FK> 外键类型
     * @param <T> 实体类型
     */
    private <PK extends Comparable<PK> & Serializable
            , FK extends Comparable<FK> & Serializable
            , T extends AbstractRegionEntity<PK, FK>> void addOrRemoveEntity(T entity, boolean add) {
        Cache cache = cacheManager.getCache(CachedType.LOCAL, entity.getClass().getName());
        if (cache == null) {
            return;
        }

        RegionEntityContext<PK, FK, T> regionEntityContext = cache.get(String.valueOf(entity.getOwner()), RegionEntityContext.class);
        if (regionEntityContext == null) {
            return;
        }

        if (add) {
            regionEntityContext.add(wrapIfNecessary(entity));
        } else {
            regionEntityContext.remove(entity);
        }
    }

    /**
     * 去包装得到原始实体数据
     * @param entity 增强实体数据
     * @param <PK> 主键类型
     * @param <T> 实体类型
     * @return 原始实体数据
     */
    private <PK extends Comparable<PK> & Serializable, T extends AbstractEntity<PK>> T unwrapIfNecessary(T entity) {
        if (entity instanceof EnhanceEntity) {
            return (T) ((EnhanceEntity<PK>) entity).getEntity();
        }
        return entity;
    }

    /**
     * 将原始实体数据包装增强
     * @param entity 原始实体数据
     * @param <PK> 主键类型
     * @param <T> 实体类型
     * @return 增强实体数据
     */
    private <PK extends Comparable<PK> & Serializable, T extends AbstractEntity<PK>> T wrapIfNecessary(T entity) {
        if (entity instanceof EnhanceEntity) {
            return entity;
        }
        return enhancer.enhance(entity);
    }
}

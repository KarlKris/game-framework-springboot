package com.li.gamecore.cache;

import com.li.gamecore.cache.core.manager.CacheManager;
import com.li.gamecore.cache.model.RegionEntityContext;
import com.li.gamecore.dao.AbstractEntity;
import com.li.gamecore.dao.AbstractRegionEntity;
import com.li.gamecore.dao.EntityBuilder;
import com.li.gamecore.dao.core.DataFinder;
import com.li.gamecore.dao.service.EntityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.Serializable;
import java.text.MessageFormat;
import java.util.List;

/**
 * @author li-yuanwen
 * @date 2022/3/8
 */
@Slf4j
@Service
public class CacheService implements EntityCacheService, RegionEntityCacheService {


    @Resource
    private EntityService entityService;
    @Resource
    private DataFinder dataFinder;
    @Resource
    private CacheManager cacheManager;


    // --------------------- EntityCacheService 实现 ----------------------------------

    @Override
    public <PK extends Comparable<PK> & Serializable, T extends AbstractEntity<PK>> T loadEntity(PK id, Class<T> tClass) {
        return entityService.load(id, tClass);
    }

    @Override
    public <PK extends Comparable<PK> & Serializable, T extends AbstractEntity<PK>> T loadOrCreate(PK id, Class<T> tClass, EntityBuilder<PK, T> entityBuilder) {
        return entityService.loadOrCreate(id, tClass, entityBuilder);
    }

    @Override
    public <PK extends Comparable<PK> & Serializable, T extends AbstractEntity<PK>> void remove(T entity) {
        entityService.remove(entity);
    }


    // --------------------- RegionEntityCacheService 实现 ----------------------------------

    private static final String ALL_BY_OWNER_HQL = "from {0} as e where e.owner = {1}";

    @Override
    public <PK extends Comparable<PK> & Serializable
            , FK extends Comparable<FK> & Serializable
            , T extends AbstractRegionEntity<PK, FK>
            , R extends RegionEntityContext<PK, FK, T>> R loadRegionContext(FK owner, Class<T> tClass, RegionEntityContextBuilder builder) {
        List<T> list = dataFinder.query(MessageFormat.format(ALL_BY_OWNER_HQL, tClass.getSimpleName(), owner), tClass);
        for (T entity : list) {
            // todo 获取持久化列表中的数据并替换
        }
        return builder.build(list);
    }


    @Override
    public <PK extends Comparable<PK> & Serializable
            , FK extends Comparable<FK> & Serializable
            , T extends AbstractRegionEntity<PK, FK>> void create(T entity) {

    }

    @Override
    public <PK extends Comparable<PK> & Serializable, FK extends Comparable<FK> & Serializable, T extends AbstractRegionEntity<PK, FK>> void remove(T entity) {

    }
}

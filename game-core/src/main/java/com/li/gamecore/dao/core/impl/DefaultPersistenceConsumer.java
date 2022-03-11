package com.li.gamecore.dao.core.impl;

import com.li.gamecore.dao.AbstractEntity;
import com.li.gamecore.dao.core.IDataAccessor;
import com.li.gamecore.dao.core.IPersistenceConsumer;
import com.li.gamecore.dao.core.IPersistenceListener;
import com.li.gamecore.dao.model.DataStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 默认的持久化消费器
 * @author li-yuanwen
 * @date 2022/3/10
 */
@Slf4j
public class DefaultPersistenceConsumer implements IPersistenceConsumer {

    /** 持久化器 **/
    private final IDataAccessor dataAccessor;
    /** 待持久化的队列 **/
    private final Queue<AbstractEntity<?>> queue;
    /** 持久化后的监听器 **/
    private final List<IPersistenceListener> listeners;

    /** 待持久化的数据 **/
    private final Map<Class<?>, Map<Object, AbstractEntity<?>>> class2EntityQueue;

    /** 持久化成功次数 **/
    private int successCount = 0;
    /** 持久化失败次数 **/
    private int failureCount = 0;


    public DefaultPersistenceConsumer(IDataAccessor dataAccessor, ScheduledExecutorService executorService
            , List<IPersistenceListener> listeners, int intervalSecond) {
        this.dataAccessor = dataAccessor;
        this.listeners = listeners;

        this.queue = new LinkedBlockingQueue<>();
        this.class2EntityQueue = new ConcurrentHashMap<>();


        // 开始执行回写任务
        executorService.scheduleWithFixedDelay(this, intervalSecond, intervalSecond, TimeUnit.SECONDS);

    }


    @Override
    public <PK extends Comparable<PK> & Serializable, T extends AbstractEntity<PK>> T findById(PK id, Class<T> tClass) {
        Map<Object, AbstractEntity<?>> map = class2EntityQueue.get(tClass);
        if (CollectionUtils.isEmpty(map)) {
            return null;
        }
        return (T) map.get(id);
    }

    @Override
    public <PK extends Comparable<PK> & Serializable, T extends AbstractEntity<PK>> void commit(T entity) {
        if (entity.isDeleteStatus() || entity.commit()) {
            this.queue.offer(entity);
            Map<Object, AbstractEntity<?>> map = this.class2EntityQueue.computeIfAbsent(entity.getClass()
                    , k -> new ConcurrentHashMap<>(64));
            map.put(entity.getId(), entity);
        }
    }

    @Override
    public void run() {
        int size = queue.size();
        for (int i = 0; i < size; i++) {
            AbstractEntity<?> entity = queue.poll();
            if (entity == null) {
                continue;
            }
            Exception exception = null;
            try {
                if (entity.isDeleteStatus()) {
                    dataAccessor.remove(entity);
                } else if (entity.swap(DataStatus.MODIFY.getCode(), DataStatus.INIT.getCode())) {
                    dataAccessor.update(entity);
                }
                successCount += 1;
            } catch (Exception e) {
                log.error("持久化发生严重异常, Class:[{}], Entity:[{}]", entity.getClass(), entity, e);
                exception = e;
                failureCount += 1;
            } finally {
                // 移除数据
                Map<Object, AbstractEntity<?>> map = class2EntityQueue.get(entity.getClass());
                map.remove(entity.getId());
                // 触发监听器
                for (IPersistenceListener listener : listeners) {
                    listener.notify(entity, exception);
                }
            }
        }
    }
}

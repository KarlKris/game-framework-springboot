package com.li.core.dao.service;

import cn.hutool.core.thread.NamedThreadFactory;
import cn.hutool.core.util.RandomUtil;
import com.li.common.event.DataBaseCloseEvent;
import com.li.core.dao.AbstractEntity;
import com.li.core.dao.core.IDataAccessor;
import com.li.core.dao.core.IPersistenceConsumer;
import com.li.core.dao.core.impl.DefaultPersistenceConsumer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * 数据库回写实现
 * @author li-yuanwen
 * @date 2022/1/25
 */
@Slf4j
@Component
@ConditionalOnBean(IDataAccessor.class)
public class DefaultDataPersistence implements IDataPersistence, ApplicationListener<DataBaseCloseEvent> {

    /** 持久化消费者工厂 **/
    private final PersistenceConsumerFactory persistenceConsumerFactory;
    /** 持久化Class2PersistenceConsumer **/
    private final ConcurrentHashMap<Class<?>, IPersistenceConsumer> consumerHolder;
    /** 数据库访问 **/
    private final IDataAccessor dataAccessor;

    public DefaultDataPersistence(@Autowired IDataAccessor dataAccessor) {
        this.dataAccessor = dataAccessor;
        // todo 后续改成可支持配置
        int threadNum = Math.min(2, Runtime.getRuntime().availableProcessors() >> 2);
        this.persistenceConsumerFactory = new PersistenceConsumerFactory(threadNum, "持久化线程");
        this.consumerHolder = new ConcurrentHashMap<>();
    }

    @Override
    public <PK extends Comparable<PK> & Serializable> void commit(AbstractEntity<PK> entity) {
        IPersistenceConsumer consumer = consumerHolder.computeIfAbsent(entity.getClass(), this::allocationPersistenceConsumer);
        consumer.commit(entity);
    }

    @Override
    public <PK extends Comparable<PK> & Serializable, T extends AbstractEntity<PK>> T findById(PK id, Class<T> tClass) {
        IPersistenceConsumer consumer = consumerHolder.get(tClass);
        return consumer == null ? null : consumer.findById(id, tClass);
    }

    @Override
    public <PK extends Comparable<PK> & Serializable, T extends AbstractEntity<PK>> Map<PK, T> findAllByClass(Class<T> tClass) {
        IPersistenceConsumer consumer = consumerHolder.get(tClass);
        if (consumer == null) {
            return Collections.emptyMap();
        }
        return consumer.findAllByClass(tClass);
    }

    private IPersistenceConsumer allocationPersistenceConsumer(Class<?> clazz) {
        ScheduledExecutorService executorService = persistenceConsumerFactory.allocation();
        // 默认5S
        return new DefaultPersistenceConsumer(dataAccessor, executorService, 5, Collections.emptyList());
    }

    @Override
    public void onApplicationEvent(DataBaseCloseEvent event) {
        log.warn("准备停止持久化线程,做最后的持久化");

        consumerHolder.values().forEach(IPersistenceConsumer::stop);
        persistenceConsumerFactory.shutdown();

        log.warn("数据库回写线程停止");
    }

    private static final class PersistenceConsumerFactory {
        /** 总线程池 **/
        private final ScheduledExecutorService[] executorServices;

        PersistenceConsumerFactory(int threadNum, String threadNamePrefix) {
            this.executorServices = new ScheduledExecutorService[threadNum];
            for (int i = 0; i < threadNum; i++) {
                this.executorServices[i] = new ScheduledThreadPoolExecutor(1
                        , new NamedThreadFactory(threadNamePrefix + "-" + i, false));
            }
        }

        ScheduledExecutorService allocation() {
            int length = executorServices.length;
            return executorServices[RandomUtil.randomInt(length)];
        }

        void shutdown() {
            for (ScheduledExecutorService executorService : executorServices) {
                executorService.shutdown();
            }
        }
    }


}

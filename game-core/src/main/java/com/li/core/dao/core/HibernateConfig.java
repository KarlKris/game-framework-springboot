package com.li.core.dao.core;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.transaction.TransactionManager;

import javax.annotation.Resource;
import javax.persistence.EntityManagerFactory;

/**
 * @author li-yuanwen
 */
@Configuration
@ConditionalOnProperty(value = "spring.datasource.url")
public class HibernateConfig implements CustomSessionFactory {


    @Resource
    @Bean
    public HibernateTemplate hibernateTemplate(EntityManagerFactory entityManagerFactory) {
        initSessionFactory(entityManagerFactory);
        return new HibernateTemplate(sessionFactory);
    }


    /**
     * 使用默认的JTATransactionManager会导致事务失败
     * 因为TransactionSynchronizationManager.bindResource()绑定的Session
     * 和CurrentSessionContext.currentSession()(SpringSessionContext)
     * 调用的TransactionSynchronizationManager.getResource()来得到Session不一致或者说得不到
     * **/
    @Bean
    @Primary
    public TransactionManager hibernateTransactionManager(EntityManagerFactory entityManagerFactory) {
        initSessionFactory(entityManagerFactory);
        return new HibernateTransactionManager(sessionFactory);
    }


    /** Hibernate5.2.x 配置SessionFactory方式,否则会出现循环引用错误,因为EntityManagerFactory也继承了SessionFactory **/
    private SessionFactory sessionFactory;

    SessionFactory sessionFactory() {
        return sessionFactory;
    }

    @Override
    public Session getSession() {
        return sessionFactory.openSession();
    }

    private void initSessionFactory(EntityManagerFactory entityManagerFactory) {
        if (sessionFactory == null) {
            sessionFactory = entityManagerFactory.unwrap(SessionFactory.class);
        }
    }

}

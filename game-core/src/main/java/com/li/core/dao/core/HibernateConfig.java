package com.li.core.dao.core;

import org.hibernate.SessionFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.TransactionManager;

import javax.persistence.EntityManagerFactory;

/**
 * @author li-yuanwen
 */
@Configuration
@ConditionalOnProperty(value = "spring.datasource.url")
public class HibernateConfig {

    @Bean
    public HibernateTemplate hibernateTemplate(EntityManagerFactory entityManagerFactory) {
        return new HibernateTemplate(entityManagerFactory.unwrap(SessionFactory.class));
    }


    @Bean
    @Primary
    public TransactionManager jpaTransactionManager(LocalContainerEntityManagerFactoryBean factoryBean) {
        return new JpaTransactionManager(factoryBean.getNativeEntityManagerFactory());
    }


}

package com.li.gamecore.dao;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManagerFactory;

/**
 * @author li-yuanwen
 */
@Configuration
public class HibernateConfig {


    @Bean
    @ConditionalOnBean(EntityManagerFactory.class)
    public SessionFactory sessionFactory(EntityManagerFactory emf) {
        return emf.unwrap(SessionFactory.class);
    }

}

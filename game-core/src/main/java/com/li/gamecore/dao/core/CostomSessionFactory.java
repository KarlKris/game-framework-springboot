package com.li.gamecore.dao.core;

import org.hibernate.Session;

/**
 * @author li-yuanwen
 * Hibernate5.2.x 配置SessionFactory方式,否则会出现循环引用错误,因为EntityManagerFactory也继承了SessionFactory
 */
public interface CostomSessionFactory {


    /**
     * 获取Session
     * @return session
     */
    Session getSession();
}

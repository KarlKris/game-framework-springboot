package com.li.core.dao.core;

import org.hibernate.Session;

/**
 * Hibernate5.2.x 配置SessionFactory方式,否则会出现循环引用错误,因为EntityManagerFactory也继承了SessionFactory
 * @author li-yuanwen
 *
 */
public interface CustomSessionFactory {


    /**
     * 获取Session
     * @return session
     */
    Session getSession();
}

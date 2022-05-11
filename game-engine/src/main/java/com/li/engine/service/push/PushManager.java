package com.li.engine.service.push;

import com.li.engine.service.session.SessionManager;
import com.li.network.anno.SocketPush;
import com.li.network.protocol.SocketProtocolManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

/**
 * @author li-yuanwen
 * 推送管理
 */
@Slf4j
@Component
public class PushManager {


    @Resource
    private SessionManager sessionManager;
    @Resource
    private SocketProtocolManager socketProtocolManager;
    @Resource
    private IPushExecutor pushExecutor;


    /** 代理对象 **/
    private final Map<String, Object> innerProxy = new HashMap<>();
    private final Map<String, Object> outerProxy = new HashMap<>();


    /** 获得推送代理对象 **/
    public <T> T getInnerPushProxy(Class<T> clz) {
        String name = clz.getName();
        Object target = this.innerProxy.get(name);
        if (target != null) {
            return (T) target;
        }
        synchronized (this.innerProxy) {
            target = this.innerProxy.get(name);
            if (target != null) {
                return (T) target;
            }

            // 非接口
            if (!clz.isInterface()) {
                throw new RuntimeException(clz.getSimpleName() + "不是推送接口");
            }

            SocketPush socketPush = AnnotationUtils.findAnnotation(clz, SocketPush.class);
            if (socketPush == null) {
                throw new RuntimeException(clz.getSimpleName() + "不是推送接口");
            }


            target = Proxy.newProxyInstance(clz.getClassLoader()
                    , new Class[]{clz}
                    , new InnerPushProxyInvoker(sessionManager, socketProtocolManager, pushExecutor));

            this.innerProxy.put(name, target);
        }
        return (T) target;
    }

    /** 获得推送代理对象 **/
    public <T> T getOuterPushProxy(Class<T> clz) {
        String name = clz.getName();
        Object target = this.outerProxy.get(name);
        if (target != null) {
            return (T) target;
        }
        synchronized (this.outerProxy) {
            target = this.outerProxy.get(name);
            if (target != null) {
                return (T) target;
            }

            // 非接口
            if (!clz.isInterface()) {
                throw new RuntimeException(clz.getSimpleName() + "不是推送接口");
            }

            SocketPush socketPush = AnnotationUtils.findAnnotation(clz, SocketPush.class);
            if (socketPush == null) {
                throw new RuntimeException(clz.getSimpleName() + "不是推送接口");
            }

            target = Proxy.newProxyInstance(clz.getClassLoader()
                    , new Class[]{clz}
                    , new OuterPushProxyInvoker(socketProtocolManager, pushExecutor));

            this.outerProxy.put(name, target);
        }
        return (T) target;
    }

}

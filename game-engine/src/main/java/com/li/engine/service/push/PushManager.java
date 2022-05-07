package com.li.engine.service.push;

import com.li.network.protocol.ProtocolMethodCtx;
import com.li.network.utils.ProtocolUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author li-yuanwen
 * 推送管理
 */
@Component
@Slf4j
public class PushManager {

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

            List<ProtocolMethodCtx> protocolMethodCtxes =
                    ProtocolUtil.getMethodCtxBySocketPush(clz);
            if (protocolMethodCtxes.isEmpty()) {
                throw new RuntimeException("接口" + clz.getSimpleName() + "没有任何推送方法");
            }

            target = Proxy.newProxyInstance(clz.getClassLoader()
                    , new Class[]{clz}
                    , new InnerPushProxyInvoker(protocolMethodCtxes));

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

            List<ProtocolMethodCtx> protocolMethodCtxes =
                    ProtocolUtil.getMethodCtxBySocketPush(clz);
            if (protocolMethodCtxes.isEmpty()) {
                throw new RuntimeException("接口" + clz.getSimpleName() + "没有任何推送方法");
            }

            target = Proxy.newProxyInstance(clz.getClassLoader()
                    , new Class[]{clz}
                    , new OuterPushProxyInvoker(protocolMethodCtxes));

            this.outerProxy.put(name, target);
        }
        return (T) target;
    }

}

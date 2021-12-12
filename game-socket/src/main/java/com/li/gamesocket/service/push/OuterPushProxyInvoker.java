package com.li.gamesocket.service.push;

import com.li.gamecommon.ApplicationContextHolder;
import com.li.gamesocket.protocol.PushResponse;
import com.li.gamesocket.protocol.serialize.SerializerHolder;
import com.li.gamesocket.service.protocol.MethodCtx;
import com.li.gamesocket.service.protocol.MethodParameter;
import com.li.gamesocket.service.protocol.impl.InBodyMethodParameter;
import com.li.gamesocket.service.protocol.impl.PushIdsMethodParameter;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.*;

/**
 * @author li-yuanwen
 * 推送外网代理实际执行对象
 */
public class OuterPushProxyInvoker implements InvocationHandler {

    /** 方法参数上下文 **/
    private final Map<Method, PushMethodCtx> methodCtxHolder;
    /** 推送执行器 **/
    private final IPushExecutor pushExecutor;

    OuterPushProxyInvoker(List<MethodCtx> methodCtxes) {
        this.methodCtxHolder = new HashMap<>(methodCtxes.size());
        methodCtxes.forEach(k -> this.methodCtxHolder.putIfAbsent(k.getMethod(), new PushMethodCtx(k)));
        this.pushExecutor = ApplicationContextHolder.getBean(IPushExecutor.class);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        PushMethodCtx pushMethodCtx = this.methodCtxHolder.get(method);
        if (pushMethodCtx == null) {
            throw new IllegalArgumentException("推送方法[" + method.getName() + "]没有添加 @SocketPush 注解");
        }
        MethodCtx methodCtx = pushMethodCtx.getMethodCtx();
        MethodParameter[] params = methodCtx.getParams();
        byte[] content = null;
        Collection<Long> targets = Collections.emptyList();
        for (int i = 0; i < args.length; i++) {
            if (params[i] instanceof InBodyMethodParameter) {
                content = SerializerHolder.DEFAULT_SERIALIZER.serialize(args[i]);
                continue;
            }

            if (params[i] instanceof PushIdsMethodParameter) {
                targets = (Collection<Long>) args[i];
            }
        }
        pushExecutor.pushToOuter(new PushResponse(targets, content), methodCtx.getProtocol());

        return null;
    }
}

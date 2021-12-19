package com.li.engine.service.push;

import com.li.gamecommon.ApplicationContextHolder;
import com.li.gamecommon.utils.ObjectsUtil;
import com.li.network.message.PushResponse;
import com.li.network.protocol.InBodyMethodParameter;
import com.li.network.protocol.MethodCtx;
import com.li.network.protocol.MethodParameter;
import com.li.network.protocol.PushIdsMethodParameter;
import com.li.network.serialize.SerializerHolder;

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
        if (ObjectsUtil.OBJECT_METHODS.contains(method)) {
            return method.invoke(proxy, args);
        }

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

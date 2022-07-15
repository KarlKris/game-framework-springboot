package com.li.engine.service.push;

import com.li.common.utils.ObjectUtils;
import com.li.network.message.PushResponse;
import com.li.network.protocol.*;
import com.li.network.serialize.SerializerHolder;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;

/**
 * @author li-yuanwen
 * 推送外网代理实际执行对象
 */
public class OuterPushProxyInvoker implements InvocationHandler {

    /** 方法参数上下文 **/
    private final SocketProtocolManager socketProtocolManager;
    /** 推送执行器 **/
    private final IPushExecutor pushExecutor;

    public OuterPushProxyInvoker(SocketProtocolManager socketProtocolManager, IPushExecutor pushExecutor) {
        this.socketProtocolManager = socketProtocolManager;
        this.pushExecutor = pushExecutor;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (ObjectUtils.OBJECT_METHODS.contains(method)) {
            return method.invoke(proxy, args);
        }

        ProtocolMethodCtx protocolMethodCtx = this.socketProtocolManager.getMethodCtxByMethod(method);
        if (protocolMethodCtx == null) {
            throw new IllegalArgumentException("推送方法[" + method.getName() + "]没有添加 @SocketPush 注解");
        }

        MethodParameter[] params = protocolMethodCtx.getParams();
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
        pushExecutor.pushToOuter(new PushResponse(targets, content), protocolMethodCtx.getProtocol());

        return null;
    }
}

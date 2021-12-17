package com.li.engine.service.push;

import com.li.engine.service.session.SessionManager;
import com.li.gamecommon.ApplicationContextHolder;
import com.li.gamecommon.utils.ObjectUtil;
import com.li.network.message.PushResponse;
import com.li.network.protocol.InBodyMethodParameter;
import com.li.network.protocol.MethodCtx;
import com.li.network.protocol.MethodParameter;
import com.li.network.protocol.PushIdsMethodParameter;
import com.li.network.serialize.SerializerHolder;
import com.li.network.session.ISession;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.*;

/**
 * @author li-yuanwen
 * 内网间推送代理实际执行对象
 */
@Slf4j
public class InnerPushProxyInvoker implements InvocationHandler {


    /** Session管理 **/
    private final SessionManager sessionManager;
    /** 方法参数上下文 **/
    private final Map<Method, PushMethodCtx> methodCtxHolder;
    /** 推送处理器 **/
    private final IPushExecutor pushExecutor;

    InnerPushProxyInvoker(List<MethodCtx> methodCtxes) {
        this.methodCtxHolder = new HashMap<>(methodCtxes.size());
        methodCtxes.forEach(k -> this.methodCtxHolder.putIfAbsent(k.getMethod(), new PushMethodCtx(k)));
        this.sessionManager = ApplicationContextHolder.getBean(SessionManager.class);
        this.pushExecutor = ApplicationContextHolder.getBean(IPushExecutor.class);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (ObjectUtil.OBJECT_METHODS.contains(method)) {
            return method.invoke(proxy, args);
        }

        PushMethodCtx pushMethodCtx = this.methodCtxHolder.get(method);
        if (pushMethodCtx == null) {
            throw new RuntimeException("推送方法[" + method.getName() + "]没有添加 @SocketPush 注解");
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

        // 构建每个Channel需要发送的目标
        Map<ISession, Set<Long>> session2Identities = new HashMap<>(targets.size());
        for (long identity : targets) {
            ISession session = sessionManager.getIdentitySession(identity);
            if (session == null) {
                continue;
            }
            session2Identities.computeIfAbsent(session, k -> new HashSet<>()).add(identity);
        }

        for (Map.Entry<ISession, Set<Long>> entry : session2Identities.entrySet()) {
            PushResponse response = new PushResponse(entry.getValue(), content);
            pushExecutor.pushToInner(entry.getKey(), response, methodCtx.getProtocol());
        }

        return null;
    }
}

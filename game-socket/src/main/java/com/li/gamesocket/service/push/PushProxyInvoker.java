package com.li.gamesocket.service.push;

import cn.hutool.core.util.ZipUtil;
import com.li.gamecore.ApplicationContextHolder;
import com.li.gamesocket.protocol.InnerMessage;
import com.li.gamesocket.protocol.MessageFactory;
import com.li.gamesocket.protocol.PushResponse;
import com.li.gamesocket.protocol.serialize.Serializer;
import com.li.gamesocket.protocol.serialize.SerializerManager;
import com.li.gamesocket.service.VocationalWorkConfig;
import com.li.gamesocket.service.command.MethodCtx;
import com.li.gamesocket.service.rpc.SnCtxManager;
import com.li.gamesocket.service.session.Session;
import com.li.gamesocket.service.session.SessionManager;
import com.li.gamesocket.utils.CommandUtils;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.*;

/**
 * @author li-yuanwen
 * 推送代理实际执行对象
 */
@Slf4j
public class PushProxyInvoker implements InvocationHandler {


    /**
     * Session管理
     **/
    private final SessionManager sessionManager = ApplicationContextHolder.getBean(SessionManager.class);
    /**
     * 方法参数上下文
     **/
    private final Map<Method, PushMethodCtx> methodCtxHolder;

    /**
     * 默认序列化/反序列化工具
     **/
    private final Serializer serializer = ApplicationContextHolder.getBean(SerializerManager.class).getDefaultSerializer();
    /**
     * 默认压缩body阙值
     **/
    private final int bodyZipLength = ApplicationContextHolder.getBean(VocationalWorkConfig.class).getBodyZipLength();
    /**
     * 消息管理器
     **/
    private final SnCtxManager snCtxManager = ApplicationContextHolder.getBean(SnCtxManager.class);

    PushProxyInvoker(List<MethodCtx> methodCtxes) {
        this.methodCtxHolder = new HashMap<>(methodCtxes.size());
        methodCtxes.forEach(k -> this.methodCtxHolder.putIfAbsent(k.getMethod(), new PushMethodCtx(k)));
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        PushMethodCtx pushMethodCtx = this.methodCtxHolder.get(method);
        if (pushMethodCtx == null) {
            throw new IllegalArgumentException("推送方法[" + method.getName() + "]没有添加 @SocketPush 注解");
        }
        MethodCtx methodCtx = pushMethodCtx.getMethodCtx();

        PushResponse pushResponse = CommandUtils.encodePushResponse(methodCtx.getParams(), args);

        // 构建每个Channel需要发送的目标
        Map<Session, Set<Long>> session2Identities = new HashMap<>(pushResponse.getTargets().size());
        for (long identity : pushResponse.getTargets()) {
            Session session = sessionManager.getIdentitySession(identity);
            if (session == null) {
                continue;
            }
            session2Identities.computeIfAbsent(session, k -> new HashSet<>()).add(identity);
        }

        for (Map.Entry<Session, Set<Long>> entry : session2Identities.entrySet()) {
            PushResponse request = new PushResponse(entry.getValue(), pushResponse.getContent());
            byte[] body = serializer.serialize(request);
            boolean zip = false;
            if (body.length > bodyZipLength) {
                body = ZipUtil.gzip(body);
                zip = true;
            }

            InnerMessage message = MessageFactory.toResponseInnerMessage(this.snCtxManager.nextSn()
                    , methodCtx.getCommand(), serializer.getSerializerType(), zip, body);

            if (log.isDebugEnabled()) {
                log.debug("推送消息[{},{}]", message.getSn(), message.getCommand());
            }

            sessionManager.writeAndFlush(entry.getKey(), message);
        }

        return null;
    }
}

package com.li.engine.service.handler;

import com.li.common.exception.SocketException;
import com.li.common.exception.code.ServerErrorCode;
import com.li.common.thread.SerializedExecutorService;
import com.li.engine.protocol.MessageFactory;
import com.li.network.message.IMessage;
import com.li.network.message.SocketProtocol;
import com.li.network.modules.ErrorCodeModule;
import com.li.network.protocol.*;
import com.li.network.serialize.Serializer;
import com.li.network.serialize.SerializerHolder;
import com.li.network.session.ISession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.util.ReflectionUtils;

import javax.annotation.Resource;
import java.util.concurrent.CompletableFuture;

/**
 * 消息分发器基类
 * @author li-yuanwen
 * @date 2021/12/9
 */
@Slf4j
public abstract class AbstractDispatcher<M extends IMessage, S extends ISession> implements Dispatcher<M, S>
        , ApplicationListener<ContextClosedEvent> {


    @Resource
    protected SerializerHolder serializerHolder;
    @Resource
    protected MessageFactory messageFactory;
    @Resource
    private SocketProtocolManager socketProtocolManager;
    @Resource
    private SerializedExecutorService executorService;

    @Override
    public void dispatch(M message, S session) {
        if (!message.isRequest()) {
            if (log.isDebugEnabled()) {
                log.debug("服务器收到响应消息,忽略");
            }
            return;
        }
        long id;
        long identity = getProtocolIdentity(session, message);
        if (identity > 0) {
            // 请求源唯一标识已绑定对象,释放线程池
            executorService.destroy(session.getSessionId());
            id = identity;
        } else {
            id = session.getSessionId();
        }
        // 交付给线程池执行
        executorService.submit(id, () -> dispatch0(session, message));
    }

    /**
     * 消息分发前处理,用于判断一下信息
     * @param session session
     * @param message message
     * @return true 可以处理
     */
    protected boolean beforeDispatch(S session, M message) {
        return true;
    }

    protected void dispatch0(S session, M message) {
        if (!beforeDispatch(session, message)) {
            return;
        }

        // 查询序列化/反序列化方式
        byte serializeType = message.getSerializeType();
        Serializer serializer = serializerHolder.getSerializer(serializeType);
        if (serializer == null) {
            if (log.isWarnEnabled()) {
                log.warn("请求消息序列化类型[{}],找不到对应的序列化工具,忽略", serializeType);
            }
            return;
        }

        SocketProtocol protocol = message.getProtocol();
        if (log.isDebugEnabled()) {
            log.debug("收到消息,协议头[{}],协议号[{},{}],消息体长度[{}]"
                    , message.getProtocolHeaderIdentity()
                    , protocol.getModule()
                    , protocol.getMethodId()
                    , message.getBody() == null ? 0 : message.getBody().length);
        }

        // 记录序列化/反序列化方式
        session.setSerializeType(serializeType);

        // 方法调用上下文
        ProtocolMethodInvokeCtx protocolMethodInvokeCtx = socketProtocolManager.getMethodInvokeCtx(protocol);
        if (protocolMethodInvokeCtx == null) {
            if (!forwardMessage(session, message)) {
                // RPC
                response(session, message, errorSocketProtocol()
                        , serializer.serialize(createErrorCodeBody(ServerErrorCode.INVALID_OP)));
            }
            return;
        }

        final long identity = getProtocolIdentity(session, message);
        // 检查身份标识
        if (protocolMethodInvokeCtx.isIdentity() && identity <= 0) {
            response(session, message, errorSocketProtocol()
                    , serializer.serialize(createErrorCodeBody(ServerErrorCode.NO_IDENTITY)));
            return;
        }

        // 将身份标识和请求序号设置进ThreadLocal,用于后续的rpc使用
        // identity可能为0,因为identity需要通过登陆或创建角色来绑定,此时的rpc协议请求应保证不会使用@Identity注解
        ThreadLocalContentHolder.setIdentity(identity);
        ThreadLocalContentHolder.setMessageSn(message.getSn());

        byte[] responseBody = null;
        try {
            Object result = invokeMethod(session, message, protocolMethodInvokeCtx);
            if (result != null) {
                if (result instanceof CompletableFuture) {
                    CompletableFuture<?> future = (CompletableFuture<?>) result;
                    responseBody = serializer.serialize(future.get());
                } else {
                    responseBody = serializer.serialize(result);
                }


            }
        }  catch (SocketException e) {
            if (log.isDebugEnabled()) {
                log.debug("发生异常请求异常,异常码[{}]", e.getErrorCode(), e);
            }
            protocol = errorSocketProtocol();
            responseBody = serializer.serialize(createExceptionBody(e));
        } catch (Exception e){
            log.error("发生未知异常", e);
            protocol = errorSocketProtocol();
            responseBody = serializer.serialize(createErrorCodeBody(ServerErrorCode.UNKNOWN));
        } finally {
            if (protocolMethodInvokeCtx.isSyncMethod()) {
                response(session, message, protocol, responseBody);
            }
            // 线程执行完成移除ThreadLocal,防止内存溢出
            ThreadLocalContentHolder.removeIdentity();
            ThreadLocalContentHolder.removeMessageSn();
        }

    }

    @Override
    public void onApplicationEvent(ContextClosedEvent event) {
        if (log.isWarnEnabled()) {
            log.warn("关闭消息分发线程池数组");
        }

        executorService.shutdown();
    }

    /**
     * 处理需要转发的信息,由服务器性质决定是否需要具有转发消息的功能
     * @param session session
     * @param message msg
     * @return true 转发成功
     */
    protected boolean forwardMessage(S session, M message) {
        return false;
    }


    /**
     * 获取消息身份标识
     * @param session session
     * @param message message
     * @return > 0 身份标识
     */
    protected abstract long getProtocolIdentity(S session, M message);

    /**
     * 消息处理逻辑调用
     * @param session session
     * @param message message
     * @param protocolMethodInvokeCtx 调用方法上下文
     * @return method.invoke()
     */
    private Object invokeMethod(S session, M message, ProtocolMethodInvokeCtx protocolMethodInvokeCtx) {
        Serializer serializer = serializerHolder.getSerializer(message.getSerializeType());

        ProtocolMethodCtx protocolMethodCtx = protocolMethodInvokeCtx.getProtocolMethodCtx();
        MethodParameter[] params = protocolMethodCtx.getParams();
        Object[] args = new Object[params.length];
        for (int i = 0; i < params.length; i++) {
            MethodParameter parameters = params[i];
            if (parameters instanceof SessionMethodParameter) {
                args[i] = session;
                continue;
            }

            if (parameters instanceof IdentityMethodParameter) {
                args[i] = getProtocolIdentity(session, message);
                continue;
            }

            if (parameters instanceof InBodyMethodParameter) {
                args[i] = serializer.deserialize(message.getBody(), parameters.getParameterClass());
                continue;
            }

            // 理论上不会运行到这行代码
            log.warn("业务消息参数解析出现未允许出现的参数类型,参数类型[{}-{}]"
                    , parameters.getClass().getSimpleName(), parameters.getParameterClass());

        }

        return ReflectionUtils.invokeMethod(protocolMethodCtx.getMethod(), protocolMethodInvokeCtx.getTarget(), args);
    }

    /**
     * 对消息message进行响应
     * @param session session
     * @param message msg
     * @param protocol 协议号
     * @param responseBody 协议号对应的消息体
     */
    protected abstract void response(S session, M message, SocketProtocol protocol, byte[] responseBody);


    private SocketProtocol errorSocketProtocol() {
        return ErrorCodeModule.ERROR_CODE_RESPONSE;
    }


    /**
     * 封装异常成消息体
     * @param exception 异常
     * @return /
     */
    private Object createExceptionBody(SocketException exception) {
        return createErrorCodeBody(exception.getErrorCode());
    }


    /**
     * 封装错误码成消息体
     * @param errorCode 错误码
     * @return /
     */
    protected Object createErrorCodeBody(int errorCode) {
        return errorCode;
    }

}

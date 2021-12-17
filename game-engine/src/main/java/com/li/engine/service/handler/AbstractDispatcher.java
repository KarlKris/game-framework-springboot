package com.li.engine.service.handler;

import cn.hutool.core.thread.NamedThreadFactory;
import cn.hutool.core.util.RandomUtil;
import com.li.engine.protocol.MessageFactory;
import com.li.engine.service.VocationalWorkConfig;
import com.li.gamecommon.exception.SocketException;
import com.li.gamecommon.exception.code.ServerErrorCode;
import com.li.gamecommon.thread.MonitoredThreadPoolExecutor;
import com.li.network.message.IMessage;
import com.li.network.message.SocketProtocol;
import com.li.network.protocol.*;
import com.li.network.serialize.Serializer;
import com.li.network.serialize.SerializerHolder;
import com.li.network.session.ISession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.util.ReflectionUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 消息分发器基类
 * @author li-yuanwen
 * @date 2021/12/9
 */
@Slf4j
public abstract class AbstractDispatcher<M extends IMessage, S extends ISession> implements Dispatcher<M, S>
        , DispatcherExecutorService, ApplicationListener<ContextClosedEvent> {


    @Resource
    protected SerializerHolder serializerHolder;
    @Resource
    protected MessageFactory messageFactory;
    @Resource
    private VocationalWorkConfig config;
    @Resource
    private SocketProtocolManager socketProtocolManager;

    /** 业务线程池 **/
    private ExecutorService[] executorServices;

    @PostConstruct
    private void init() {
        // 保证线程池数量是2的N次幂
        int i = (Runtime.getRuntime().availableProcessors() >> 1) << 2;
        this.executorServices = new ExecutorService[i];
        for (int j = 0; j < i; j++) {
            // 单线程池,减少加锁频率
            this.executorServices[j] = new MonitoredThreadPoolExecutor(1, 1,
                    0, TimeUnit.SECONDS
                    , new ArrayBlockingQueue<>(config.getMaxQueueLength())
                    , new NamedThreadFactory("消息分发线程池", false));
        }
    }

    @Override
    public void execute(Runnable runnable) {
        this.executorServices[RandomUtil.randomInt(this.executorServices.length)].submit(runnable);
    }

    /**
     * 根据id值来hash线程池,并提交任务
     * @param id id
     * @param runnable 任务
     */
    @Override
    public void execute(long id, Runnable runnable) {
        this.executorServices[calExecutorServiceArrayIndex(id)].submit(runnable);
    }


    /** 根据hash找到对应的线程池下标,仿HashMap **/
    private int calExecutorServiceArrayIndex(Long id) {
        int length = this.executorServices.length;
        return (length - 1) & hash(id);
    }

    @Override
    public void dispatch(M message, S session) {
        if (!message.isRequest()) {
            if (log.isDebugEnabled()) {
                log.debug("服务器收到响应消息,忽略");
            }
            return;
        }
        // 交付给线程池执行
        execute(getIdBySessionAndMessage(session, message), () -> dispatch0(session, message));
    }

    /**
     * 根据session和message返回消息来源的玩家唯一标识
     * @param session session
     * @param message message
     * @return 消息来源的玩家唯一标识 or sessionId
     */
    protected abstract long getIdBySessionAndMessage(S session, M message);

    protected void dispatch0(S session, M message) {
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
        MethodInvokeCtx methodInvokeCtx = socketProtocolManager.getMethodInvokeCtx(protocol);
        if (methodInvokeCtx == null) {
            // RPC
            if (!forwardMessage(session, message)) {
                response(session, message, errorSocketProtocol()
                        , serializer.serialize(createErrorCodeBody(ServerErrorCode.INVALID_OP)));
            }
            return;
        }

        // 检查身份标识
        if (methodInvokeCtx.isIdentity() && getProtocolIdentity(session, message) <= 0) {
            response(session, message, errorSocketProtocol()
                    , serializer.serialize(createErrorCodeBody(ServerErrorCode.NO_IDENTITY)));
            return;
        }

        // 将身份标识设置进ThreadLocal
        setIdentityToThreadLocal(session, message);
        byte[] responseBody = null;
        try {
            Object result = invokeMethod(session, message, methodInvokeCtx);
            if (result != null) {
                responseBody = serializer.serialize(result);
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
            response(session, message, protocol, responseBody);
            // 线程执行完成移除ThreadLocal,防止内存溢出
            ThreadSessionIdentityHolder.remove();
        }

    }

    @Override
    public void onApplicationEvent(ContextClosedEvent event) {
        if (log.isWarnEnabled()) {
            log.warn("关闭消息分发线程池数组");
        }

        for (ExecutorService executorService : this.executorServices) {
            executorService.shutdown();
        }
    }


    /**
     * 计算hash值
     * @param key key
     * @return hash
     */
    static int hash(Long key) {
        int h;
        return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
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
     * ThreadLocal<Long> 存储玩家标识
     * @param session session
     * @param message message
     */
    protected abstract void setIdentityToThreadLocal(S session, M message);

    /**
     * 获取消息身份标识
     * @param session session
     * @param message message
     * @return > 0 身份标识 or 无
     */
    protected abstract long getProtocolIdentity(S session, M message);

    /**
     * 消息处理逻辑调用
     * @param session session
     * @param message message
     * @param methodInvokeCtx 调用方法上下文
     * @return method.invoke()
     */
    private Object invokeMethod(S session, M message, MethodInvokeCtx methodInvokeCtx) {
        Serializer serializer = serializerHolder.getSerializer(message.getSerializeType());

        MethodCtx methodCtx = methodInvokeCtx.getMethodCtx();
        MethodParameter[] params = methodCtx.getParams();
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

        return ReflectionUtils.invokeMethod(methodCtx.getMethod(), methodInvokeCtx.getTarget(), args);
    }

    /**
     * 对消息message进行响应
     * @param session session
     * @param message msg
     * @param protocol 协议号
     * @param responseBody 协议号对应的消息体
     */
    protected abstract void response(S session, M message, SocketProtocol protocol, byte[] responseBody);


    /**
     * 返回错误码协议号
     * @return 错误码协议号
     */
    protected abstract SocketProtocol errorSocketProtocol();


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

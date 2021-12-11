package com.li.gamesocket.service.handler;

import cn.hutool.core.thread.NamedThreadFactory;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.ZipUtil;
import com.li.gamecommon.exception.SocketException;
import com.li.gamecommon.exception.code.ServerErrorCode;
import com.li.gamecommon.thread.MonitoredThreadPoolExecutor;
import com.li.gamesocket.protocol.IMessage;
import com.li.gamesocket.protocol.Response;
import com.li.gamesocket.protocol.serialize.Serializer;
import com.li.gamesocket.protocol.serialize.SerializerHolder;
import com.li.gamesocket.service.VocationalWorkConfig;
import com.li.gamesocket.service.protocol.MethodInvokeCtx;
import com.li.gamesocket.service.protocol.SocketProtocol;
import com.li.gamesocket.service.protocol.SocketProtocolManager;
import com.li.gamesocket.service.session.ISession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;

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
        , DispatcherExecutorService<S>, ApplicationListener<ContextClosedEvent> {


    @Resource
    protected SerializerHolder serializerHolder;

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
    protected void submit(long id, Runnable runnable) {
        this.executorServices[calExecutorServiceArrayIndex(id)].submit(runnable);
    }


    /** 根据hash找到对应的线程池下标,仿HashMap **/
    private int calExecutorServiceArrayIndex(Long id) {
        int length = this.executorServices.length;
        return (length - 1) & hash(id);
    }


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
                response(session, message, false, serializeType, serializer.serialize(Response.INVALID_OP));
            }
            return;
        }

        // 将身份标识设置进ThreadLocal
        setIdentityToThreadLocal(session, message);

        byte[] responseBody = null;
        boolean zip = false;
        try {
            Object result = invokeMethod(session, message, methodInvokeCtx);
            if (result != null) {
                responseBody = serializer.serialize(result);
                zip = responseBody.length > config.getBodyZipLength();
                if (zip) {
                    responseBody = ZipUtil.gzip(responseBody);
                }
            }
        }  catch (SocketException e) {
            if (log.isDebugEnabled()) {
                log.debug("发生异常请求异常,异常码[{}]", e.getErrorCode(), e);
            }
            responseBody = serializer.serialize(createExceptionBody(e));
        } catch (Exception e){
            log.error("发生未知异常", e);
            responseBody = serializer.serialize(createErrorCodeBody(ServerErrorCode.UNKNOWN));
        } finally {
            response(session, message, zip, serializer.getSerializerType(), responseBody);
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
     * 消息处理逻辑调用
     * @param session session
     * @param message message
     * @param methodInvokeCtx 调用方法上下文
     * @return method.invoke()
     */
    protected abstract Object invokeMethod(S session, M message, MethodInvokeCtx methodInvokeCtx);

    /**
     * 对消息message进行响应
     * @param session session
     * @param message msg
     * @param zip 压缩标识
     * @param serializeType 序列化方式
     * @param responseBody 消息体
     */
    protected abstract void response(S session, M message, boolean zip, byte serializeType, byte[] responseBody);


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
    protected abstract Object createErrorCodeBody(int errorCode);

}

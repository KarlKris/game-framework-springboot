package com.li.gamesocket.service.handler.impl;

import cn.hutool.core.convert.ConvertException;
import cn.hutool.core.thread.NamedThreadFactory;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.ZipUtil;
import com.li.gamecommon.exception.BadRequestException;
import com.li.gamecommon.exception.SerializeFailException;
import com.li.gamecommon.thread.MonitoredThreadPoolExecutor;
import com.li.gamesocket.channelhandler.ChannelAttributeKeys;
import com.li.gamesocket.protocol.*;
import com.li.gamesocket.protocol.serialize.Serializer;
import com.li.gamesocket.protocol.serialize.SerializerManager;
import com.li.gamesocket.service.VocationalWorkConfig;
import com.li.gamesocket.service.command.Command;
import com.li.gamesocket.service.command.CommandManager;
import com.li.gamesocket.service.command.MethodCtx;
import com.li.gamesocket.service.command.MethodInvokeCtx;
import com.li.gamesocket.service.handler.Dispatcher;
import com.li.gamesocket.service.handler.DispatcherExecutorService;
import com.li.gamesocket.service.rpc.RpcService;
import com.li.gamesocket.service.session.Session;
import com.li.gamesocket.service.session.SessionManager;
import com.li.gamesocket.utils.CommandUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

import javax.annotation.PostConstruct;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author li-yuanwen
 * @date 2021/7/31 15:44
 **/
@Component
@Slf4j
public class DispatcherImpl implements Dispatcher, DispatcherExecutorService,  ApplicationListener<ContextClosedEvent> {


    @Autowired
    private VocationalWorkConfig config;
    @Autowired
    private CommandManager commandManager;
    @Autowired
    private SessionManager sessionManager;
    @Autowired
    private SerializerManager serializerManager;

    @Autowired
    private RpcService rpcService;

    /** ??????????????? **/
    private ExecutorService[] executorServices;


    @PostConstruct
    private void init() {

        // ????????????????????????2???N??????
        int i = (Runtime.getRuntime().availableProcessors() >> 1) << 1;
        this.executorServices = new ExecutorService[i];
        for (int j = 0; j < i; j++) {
            // ????????????,??????????????????
            this.executorServices[j] = new MonitoredThreadPoolExecutor(1, 1,
                    0, TimeUnit.SECONDS
                    , new ArrayBlockingQueue<>(config.getMaxQueueLength())
                    , new NamedThreadFactory("???????????????", false));
        }
    }

    @Override
    public void execute(Session session, Runnable runnable) {
        long id = session.getSessionId();
        if (session.identity()) {
            id = session.getIdentity();
        }

        int index = canAndGetExecutorServiceArrayIndex(hash(id));
        this.executorServices[index].submit(runnable);
    }

    @Override
    public void execute(Runnable runnable) {
        this.executorServices[RandomUtil.randomInt(this.executorServices.length)].submit(runnable);
    }

    @Override
    public void dispatch(IMessage message, Session session) {
        if (!message.isRequest()) {
            if (log.isDebugEnabled()) {
                log.debug("???????????????????????????,??????");
            }
            return;
        }
        // ????????????
        execute(session, () -> doDispatch(session, message));
    }

    /** ???????????? **/
    private void doDispatch(Session session, IMessage message) {
        // ???????????????/??????????????????
        byte serializeType = message.getSerializeType();
        Serializer serializer = serializerManager.getSerializer(serializeType);
        if (serializer == null) {
            if (log.isWarnEnabled()) {
                log.warn("???????????????????????????[{}],?????????????????????????????????,??????", serializeType);
            }
            return;
        }

        Command command = message.getCommand();
        if (log.isDebugEnabled()) {
            log.debug("????????????[{}-{}],?????????[{}]", command.getModule(), command.getInstruction(), message.getProtocolHeaderIdentity());
        }

        // ???????????????/??????????????????
        session.getChannel().attr(ChannelAttributeKeys.LAST_SERIALIZE_TYPE).set(serializeType);

        // ?????????????????????
        MethodInvokeCtx methodInvokeCtx = commandManager.getMethodInvokeCtx(command);
        if (methodInvokeCtx == null) {
            // RPC
            if (!this.rpcService.forward(session, message)) {
                response(session, message, false, serializer.getSerializerType(), serializer.serialize(Response.INVALID_OP));
            }
            return;
        }

        MethodCtx methodCtx = methodInvokeCtx.getMethodCtx();
        Class<?> returnType = methodCtx.getMethod().getReturnType();
        boolean noResponse = ClassUtils.isAssignable(Void.TYPE, returnType);

        byte[] body = message.getBody();
        if (message.zip()) {
            body = ZipUtil.unGzip(body);
        }

        byte[] responseBody = null;
        boolean zip = false;
        try {
            Request request = serializer.deserialize(body, Request.class);

            Object result = null;
            if (message.isInnerMessage()) {
                // ????????????????????????????????????????????????
                if (methodInvokeCtx.isIdentity() && !request.hasIdentity()) {
                    // ????????????
                    responseBody = serializer.serialize(Response.NO_IDENTITY);
                    return;
                }

                Long identity;
                if ((identity = request.getIdentity()) != null && sessionManager.online(identity)) {
                    // ????????????
                    sessionManager.bindIdentity(session, request.getIdentity(), true);
                }

                Object[] args = CommandUtils.decodeRequest(session, -1, methodCtx.getParams(), request);
                result = ReflectionUtils.invokeMethod(methodCtx.getMethod(), methodInvokeCtx.getTarget(), args);
            } else {
                // ????????????????????????????????????Session???
                if (methodInvokeCtx.isIdentity() && !session.identity()) {
                    // ????????????
                    responseBody = serializer.serialize(Response.NO_IDENTITY);
                    return;
                }

                result = ReflectionUtils.invokeMethod(methodCtx.getMethod(), methodInvokeCtx.getTarget()
                        , CommandUtils.decodeRequest(session, session.getIdentity(), methodCtx.getParams(), request));
            }

            Response<Object> response;
            if (!noResponse) {
                // ??????
                if (result instanceof Response) {
                    response = (Response<Object>) result;
                }else {
                    response = Response.SUCCESS(result);
                }
            } else {
                response = Response.DEFAULT_SUCCESS;
            }
            responseBody = serializer.serialize(response);
            zip = responseBody.length > config.getBodyZipLength();
            if (zip) {
                responseBody = ZipUtil.gzip(responseBody);
            }
        } catch (SerializeFailException e) {
            log.error("???????????????/??????????????????", e);
            responseBody = serializer.serialize(Response.SERIALIZE_FAIL);
        } catch (ConvertException e) {
            log.error("????????????????????????", e);
            responseBody = serializer.serialize(Response.CONVERT_FAIL);
        } catch (IllegalArgumentException e) {
            log.error("????????????????????????", e);
            responseBody = serializer.serialize(Response.PARAM_ANALYSIS_ERROR);
        } catch (BadRequestException e) {
            if (log.isDebugEnabled()) {
                log.debug("????????????????????????,?????????[{}]", e.getErrorCode(), e);
            }
            responseBody = serializer.serialize(Response.ERROR(e.getErrorCode()));
        } catch (Exception e){
            log.error("??????????????????", e);
            responseBody = serializer.serialize(Response.UNKNOWN);
        } finally {
            response(session, message, zip, serializer.getSerializerType(), responseBody);
        }
    }

    /** ?????? **/
    private void response(Session session, IMessage requestMessage, boolean zip, byte serializeType, byte[] responseBody) {
        IMessage message = null;
        if (requestMessage.isInnerMessage()) {
            message = MessageFactory.toInnerMessage(requestMessage.getSn()
                    , ProtocolConstant.transformResponse(requestMessage.getMessageType())
                    , requestMessage.getCommand()
                    , serializeType
                    , zip
                    , responseBody
                    , session.ip());
        }else if (requestMessage.isOuterMessage()) {
            message = MessageFactory.toOuterMessage(requestMessage.getSn()
                    , ProtocolConstant.transformResponse(requestMessage.getMessageType())
                    , requestMessage.getCommand()
                    , serializeType
                    , zip
                    , responseBody);
        }
        sessionManager.writeAndFlush(session, message);
    }


    /** ??????hash??????????????????????????????,???HashMap **/
    private int canAndGetExecutorServiceArrayIndex(int hash) {
        int length = this.executorServices.length;
        return (length - 1) & hash;
    }

    // ??????hash???
    private static int hash(Long key) {
        int h;
        return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
    }

    @Override
    public void onApplicationEvent(ContextClosedEvent contextClosedEvent) {
        if (log.isWarnEnabled()) {
            log.warn("?????????????????????????????????");
        }

        for (ExecutorService executorService : this.executorServices) {
            executorService.shutdown();
        }
    }
}

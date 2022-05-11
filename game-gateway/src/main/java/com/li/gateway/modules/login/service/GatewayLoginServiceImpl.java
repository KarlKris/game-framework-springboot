package com.li.gateway.modules.login.service;

import com.li.common.exception.BadRequestException;
import com.li.common.exception.code.ServerErrorCode;
import com.li.engine.service.handler.ThreadLocalContentHolder;
import com.li.engine.service.push.ResponseMessagePushProcessor;
import com.li.engine.service.rpc.IRpcService;
import com.li.engine.service.session.SessionManager;
import com.li.network.message.SocketProtocol;
import com.li.network.modules.ErrorCodeModule;
import com.li.network.session.ISession;
import com.li.network.session.PlayerSession;
import com.li.protocol.game.login.protocol.GameServerLoginController;
import com.li.protocol.game.login.vo.ReqGameCreateAccount;
import com.li.protocol.game.login.vo.ReqGameLoginAccount;
import com.li.protocol.game.login.vo.ResGameCreateAccount;
import com.li.protocol.game.login.vo.ResGameLoginAccount;
import com.li.protocol.gateway.login.protocol.GatewayLoginModule;
import com.li.protocol.gateway.login.vo.ResGatewayCreateAccount;
import com.li.protocol.gateway.login.vo.ResGatewayLoginAccount;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

/**
 * @author li-yuanwen
 */
@Service
@Slf4j
public class GatewayLoginServiceImpl implements GatewayLoginService {

    @Resource
    private IRpcService rpcService;
    @Resource
    private SessionManager sessionManager;
    @Resource
    private ResponseMessagePushProcessor<PlayerSession> responseMessagePushProcessor;

    @Override
    public void create(PlayerSession session, String account, int channel, int serverId) {
        GameServerLoginController sendProxy = rpcService.getSendProxy(GameServerLoginController.class, String.valueOf(serverId));
        CompletableFuture<ResGameCreateAccount> future = sendProxy.create(null, new ReqGameCreateAccount(account, channel));
        future.whenComplete((result, throwable) -> {
            if (throwable != null) {
                if (throwable instanceof BadRequestException) {
                    BadRequestException exception = (BadRequestException) throwable;
                    responseMessagePushProcessor.response(session, ThreadLocalContentHolder.getMessageSn()
                            , ErrorCodeModule.ERROR_CODE_RESPONSE, exception.getErrorCode());
                } else {
                    responseMessagePushProcessor.response(session, ThreadLocalContentHolder.getMessageSn()
                            , ErrorCodeModule.ERROR_CODE_RESPONSE, ServerErrorCode.UNKNOWN);

                }
                return;
            }

            long identity = result.getIdentity();

            // 绑定身份
            sessionManager.bindIdentity(session, identity);

            if (log.isDebugEnabled()) {
                log.debug("网关服请求游戏服[{}]创建账号成功,绑定session[{},{}]", serverId, session.getSessionId(), identity);
            }

            responseMessagePushProcessor.response(session, ThreadLocalContentHolder.getMessageSn()
                    , new SocketProtocol(GatewayLoginModule.MODULE, GatewayLoginModule.CREATE_ACCOUNT)
                    , new ResGatewayCreateAccount(identity));
        });

    }

    @Override
    public void login(PlayerSession session, String account, int channel, int serverId) {
        GameServerLoginController sendProxy = rpcService.getSendProxy(GameServerLoginController.class, String.valueOf(serverId));
        CompletableFuture<ResGameLoginAccount> future = sendProxy.login(null, new ReqGameLoginAccount(account, channel));
        future.whenComplete((result, throwable) -> {
            if (throwable != null) {
                if (throwable instanceof BadRequestException) {
                    BadRequestException exception = (BadRequestException) throwable;
                    responseMessagePushProcessor.response(session, ThreadLocalContentHolder.getMessageSn()
                            , ErrorCodeModule.ERROR_CODE_RESPONSE, exception.getErrorCode());
                } else {
                    responseMessagePushProcessor.response(session, ThreadLocalContentHolder.getMessageSn()
                            , ErrorCodeModule.ERROR_CODE_RESPONSE, ServerErrorCode.UNKNOWN);

                }
                return;
            }

            long identity = result.getIdentity();

            ISession oldSession = sessionManager.bindIdentity(session, identity);
            if (oldSession != null && !Objects.equals(oldSession.getSessionId(), session.getSessionId())) {
                // 断开先前连接
                sessionManager.kickOut(oldSession);
            }

            if (log.isDebugEnabled()) {
                log.debug("网关服请求游戏服[{}]登录账号成功,绑定session[{},{}]", serverId, session.getSessionId(), new ResGatewayLoginAccount(identity));
            }

            responseMessagePushProcessor.response(session, ThreadLocalContentHolder.getMessageSn()
                    , new SocketProtocol(GatewayLoginModule.MODULE, GatewayLoginModule.LOGIN_ACCOUNT)
                    , new ResGatewayLoginAccount(identity));

        });

    }
}

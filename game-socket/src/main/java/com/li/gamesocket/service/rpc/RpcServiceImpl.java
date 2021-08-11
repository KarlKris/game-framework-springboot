package com.li.gamesocket.service.rpc;

import com.li.gamecore.exception.BadRequestException;
import com.li.gamecore.exception.code.ResultCode;
import com.li.gamecore.rpc.RemoteServerSeekService;
import com.li.gamecore.rpc.model.Address;
import com.li.gamesocket.client.NioNettyClient;
import com.li.gamesocket.client.NioNettyClientFactory;
import com.li.gamesocket.protocol.IMessage;
import com.li.gamesocket.protocol.InnerMessage;
import com.li.gamesocket.protocol.MessageFactory;
import com.li.gamesocket.service.session.Session;
import com.li.gamesocket.utils.CommandUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author li-yuanwen
 * 远程调用服务
 */
@Service
@Slf4j
public class RpcServiceImpl implements RpcService {

    @Autowired
    private NioNettyClientFactory clientFactory;
    @Autowired(required = false)
    private RemoteServerSeekService remoteServerSeekService;
    @Autowired(required = false)
    private SnCtxManager snCtxManager;

    @Override
    public boolean forward(Session session, IMessage message) {
        checkAndThrowRemoteService();

        Address address = this.remoteServerSeekService.seekApplicationAddressByModule(message.getCommand().getModule()
                , session.getIdentity());

        if (address == null) {
            return false;
        }

        NioNettyClient client = clientFactory.newInstance(address);
        long nextSn = snCtxManager.nextSn();
        // 构建内部消息进行转发
        InnerMessage innerMessage = MessageFactory.toForwardMessage(message, nextSn, session);

        try {
            client.send(innerMessage
                    , (msg, completableFuture)
                            -> snCtxManager.forward(msg.getSn(), nextSn, session.getChannel()));
            return true;
        } catch (InterruptedException e) {
            log.error("消息转发至[{}]发生未知异常", address, e);
            return false;
        }
    }


    @Override
    public <T> T getSendProxy(Class<T> tClass, long identity) {
        checkAndThrowRemoteService();

        short module = CommandUtils.getModule(tClass);
        Address address = this.remoteServerSeekService.seekApplicationAddressByModule(module
                , identity);
        if (address == null) {
            throw new BadRequestException(ResultCode.INVALID_OP);
        }

        return clientFactory.newInstance(address).getSendProxy(tClass);
    }

    @Override
    public <T> T getSendProxy(Class<T> tClass, String serverId) {
        checkAndThrowRemoteService();

        short module = CommandUtils.getModule(tClass);
        Address address = this.remoteServerSeekService.seekApplicationAddressById(module
                , serverId);
        if (address == null) {
            throw new BadRequestException(ResultCode.INVALID_OP);
        }

        return clientFactory.newInstance(address).getSendProxy(tClass);
    }

    private void checkAndThrowRemoteService() {
        if (this.remoteServerSeekService == null) {
            throw new BadRequestException(ResultCode.CANT_CONNECT_REMOTE);
        }
    }
}

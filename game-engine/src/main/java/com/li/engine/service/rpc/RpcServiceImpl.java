package com.li.engine.service.rpc;

import com.li.gamecommon.exception.BadRequestException;
import com.li.gamecommon.exception.code.ServerErrorCode;
import com.li.gamecommon.rpc.RemoteServerSeekService;
import com.li.gamecommon.rpc.model.Address;
import com.li.engine.client.NioNettyClientFactory;
import com.li.network.utils.ProtocolUtil;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 远程调用服务
 * @author li-yuanwen
 * @date 2021/12/13
 */
@Component
public class RpcServiceImpl implements IRpcService {


    @Resource
    private RemoteServerSeekService remoteServerSeekService;
    @Resource
    private NioNettyClientFactory clientFactory;


    @Override
    public <T> T getSendProxy(Class<T> tClass, long identity) {
        checkAndThrowRemoteService();

        short module = ProtocolUtil.getProtocolModuleByClass(tClass);
        Address address = remoteServerSeekService.seekApplicationAddressByModule(module
                , identity);
        if (address == null) {
            throw new BadRequestException(ServerErrorCode.INVALID_OP);
        }

        return clientFactory.connectTo(address).getSendProxy(tClass);
    }

    @Override
    public <T> T getSendProxy(Class<T> tClass, String serverId) {
        checkAndThrowRemoteService();

        short module = ProtocolUtil.getProtocolModuleByClass(tClass);
        Address address = this.remoteServerSeekService.seekApplicationAddressById(module
                , serverId);
        if (address == null) {
            throw new BadRequestException(ServerErrorCode.INVALID_OP);
        }

        return clientFactory.connectTo(address).getSendProxy(tClass);
    }

    private void checkAndThrowRemoteService() {
        if (this.remoteServerSeekService == null) {
            throw new BadRequestException(ServerErrorCode.CANT_CONNECT_REMOTE);
        }
    }
}

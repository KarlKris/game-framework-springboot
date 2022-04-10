package com.li.engine.service.rpc;

import com.li.engine.client.NioNettyClientFactory;
import com.li.common.exception.BadRequestException;
import com.li.common.exception.code.ServerErrorCode;
import com.li.common.rpc.RemoteServerSeekService;
import com.li.common.rpc.model.Address;
import com.li.network.protocol.SocketProtocolManager;
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
    @Resource
    private SocketProtocolManager protocolManager;


    @Override
    public <T> T getSendProxy(Class<T> tClass, long identity) {
        checkAndThrowRemoteService();

        short module = ProtocolUtil.getProtocolModuleByClass(tClass);
        if (protocolManager.getProtocolModules().contains(module)) {
            throw new RuntimeException("不允许同类型进程间互连");
        }

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
        if (protocolManager.getProtocolModules().contains(module)) {
            throw new RuntimeException("不允许同类型进程间互连");
        }

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

package com.li.engine.server;

import com.li.common.rpc.LocalServerService;
import com.li.common.rpc.model.ServerInfo;
import com.li.common.utils.IpUtils;
import com.li.network.protocol.SocketProtocolManager;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.net.SocketException;

/**
 * @author li-yuanwen
 * @date 2021/8/7 23:08
 **/
@Service
public class LocalServerServiceImpl implements LocalServerService {

    @Resource
    private SocketProtocolManager protocolManager;
    @Resource
    private ServerConfig serverConfig;


    @Override
    public ServerInfo getLocalServerInfo() throws SocketException {
        return new ServerInfo(String.valueOf(serverConfig.getServerId())
                , IpUtils.getLocalIpAddress()
                , serverConfig.getPort()
                , protocolManager.getProtocolModules() );
    }
}

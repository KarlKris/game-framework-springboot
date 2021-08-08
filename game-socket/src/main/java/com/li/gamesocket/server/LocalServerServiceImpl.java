package com.li.gamesocket.server;

import com.li.gamecore.rpc.LocalServerService;
import com.li.gamecore.rpc.model.ServerInfo;
import com.li.gamecore.utils.IpUtils;
import com.li.gamesocket.service.command.CommandManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.SocketException;

/**
 * @author li-yuanwen
 * @date 2021/8/7 23:08
 **/
@Service
public class LocalServerServiceImpl implements LocalServerService {

    @Autowired
    private CommandManager commandManager;
    @Autowired
    private ServerProperties serverProperties;

    @Override
    public ServerInfo getLocalServerInfo() throws SocketException {
        return new ServerInfo(serverProperties.getId()
                , IpUtils.getLocalIpAddress()
                , serverProperties.getPort()
                , commandManager.getModules());
    }
}

package com.li.client.handler.impl;

import com.li.client.controller.MainController;
import com.li.client.controller.MessageController;
import com.li.client.handler.AbstractProtocolResponseBodyHandler;
import com.li.client.ui.UiType;
import com.li.network.message.SocketProtocol;
import com.li.protocol.gateway.login.protocol.GatewayLoginModule;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author li-yuanwen
 * @date 2022/5/10
 */
@Component
public class LoginProtocolResponseBodyHandler extends AbstractProtocolResponseBodyHandler<Long> {

    @Resource
    private MessageController messageController;
    @Resource
    private MainController mainController;

    @Override
    protected void handle0(Long responseBody) {
        messageController.addInfoMessage(responseBody.toString());
        mainController.switchUI(UiType.PLAYER_DETAILS);
    }

    @Override
    protected void error(long errorCode) {
        messageController.addErrorMessage(String.valueOf(errorCode));
    }

    @Override
    public SocketProtocol[] getSocketProtocol() {
        return new SocketProtocol[] {
                new SocketProtocol(GatewayLoginModule.MODULE, GatewayLoginModule.GAME_SERVER_CREATE),
                new SocketProtocol(GatewayLoginModule.MODULE, GatewayLoginModule.GAME_SERVER_LOGIN)
        };
    }
}

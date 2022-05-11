package com.li.client.handler.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.li.client.controller.MainController;
import com.li.client.controller.MessageController;
import com.li.client.handler.AbstractProtocolResponseBodyHandler;
import com.li.client.ui.UiType;
import com.li.network.message.SocketProtocol;
import com.li.protocol.gateway.login.protocol.GatewayLoginModule;
import com.li.protocol.gateway.login.vo.ResGatewayCreateAccount;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author li-yuanwen
 * @date 2022/5/10
 */
@Component
public class CreateAccountProtocolResponseBodyHandler extends AbstractProtocolResponseBodyHandler<ResGatewayCreateAccount> {

    @Resource
    private MessageController messageController;
    @Resource
    private MainController mainController;
    @Resource
    private ObjectMapper objectMapper;

    @Override
    protected void handle0(ResGatewayCreateAccount responseBody) {
        try {
            messageController.addInfoMessage(objectMapper.writeValueAsString(responseBody));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        mainController.switchUI(UiType.PLAYER_DETAILS);
    }

    @Override
    protected void error(long errorCode) {
        messageController.addErrorMessage(String.valueOf(errorCode));
    }

    @Override
    public SocketProtocol[] getSocketProtocol() {
        return new SocketProtocol[] {
                new SocketProtocol(GatewayLoginModule.MODULE, GatewayLoginModule.CREATE_ACCOUNT)
        };
    }
}

package com.li.client.handler.impl;

import com.li.client.controller.ChatController;
import com.li.client.handler.AbstractProtocolResponseBodyHandler;
import com.li.network.message.SocketProtocol;
import com.li.protocol.game.chat.protocol.ChatModule;
import com.li.protocol.game.chat.vo.ChatContent;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author li-yuanwen
 * @date 2022/5/17
 */
@Component
public class ChatMessagePushContentHandler extends AbstractProtocolResponseBodyHandler<ChatContent> {

    @Resource
    private ChatController chatController;

    @Override
    protected void handle0(ChatContent responseBody) {
        chatController.addMessage(responseBody.getSenderId(), responseBody.getMsg());
    }

    @Override
    public SocketProtocol[] getSocketProtocol() {
        return new SocketProtocol[] {
                new SocketProtocol(ChatModule.MODULE, ChatModule.PUSH)
        };
    }
}

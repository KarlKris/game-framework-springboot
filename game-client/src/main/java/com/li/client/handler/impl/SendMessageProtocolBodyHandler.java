package com.li.client.handler.impl;

import com.li.client.handler.AbstractProtocolResponseBodyHandler;
import com.li.network.message.SocketProtocol;
import com.li.protocol.game.chat.protocol.ChatModule;
import org.springframework.stereotype.Component;

/**
 * 发送聊天信息
 * @author li-yuanwen
 * @date 2022/5/17
 */
@Component
public class SendMessageProtocolBodyHandler extends AbstractProtocolResponseBodyHandler<Void> {

    @Override
    protected void handle0(Void responseBody) {
        messageController.addInfoMessage("消息发送成功");
    }

    @Override
    public SocketProtocol[] getSocketProtocol() {
        return new SocketProtocol[] {
            new SocketProtocol(ChatModule.MODULE, ChatModule.SEND)
        };
    }
}

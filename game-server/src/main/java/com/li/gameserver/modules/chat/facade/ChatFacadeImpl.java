package com.li.gameserver.modules.chat.facade;

import com.li.gameserver.modules.chat.service.ChatService;
import com.li.protocol.game.chat.protocol.ChatFacade;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author li-yuanwen
 */
@Component
public class ChatFacadeImpl implements ChatFacade {

    @Resource
    private ChatService chatService;

    @Override
    public void send(long identity, String msg) {
        chatService.send(identity, msg);
    }
}

package com.li.gameserver.modules.chat.facade;

import com.li.gameserver.modules.chat.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author li-yuanwen
 */
@Component
public class ChatFacadeImpl implements ChatFacade {

    @Autowired
    private ChatService chatService;

    @Override
    public void send(long identity, String msg) {
        chatService.send(identity, msg);
    }
}

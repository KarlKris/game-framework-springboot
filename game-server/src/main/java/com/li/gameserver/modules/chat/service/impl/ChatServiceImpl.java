package com.li.gameserver.modules.chat.service.impl;

import com.li.gameremote.modules.chat.facade.ChatPush;
import com.li.gameremote.modules.chat.vo.ChatContent;
import com.li.gameserver.modules.chat.service.ChatService;
import com.li.gamesocket.anno.InnerPushInject;
import com.li.gamesocket.service.session.SessionManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;

/**
 * @author li-yuanwen
 */
@Slf4j
@Service
public class ChatServiceImpl implements ChatService {

    @Autowired
    private SessionManager sessionManager;

    @InnerPushInject
    private ChatPush chatPush;

    @Override
    public void send(long identity, String msg) {
        Collection<Long> onlineIdentities =
                sessionManager.getOnlineIdentities();
        ChatContent content = ChatContent.of(identity, msg);
        chatPush.pushMessage(onlineIdentities, content);
    }
}

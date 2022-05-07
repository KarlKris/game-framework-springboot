package com.li.game.modules.chat.service.impl;

import com.li.engine.anno.InnerPushInject;
import com.li.engine.service.session.SessionManager;
import com.li.game.modules.chat.service.ChatService;
import com.li.protocol.game.chat.protocol.ChatPush;
import com.li.protocol.game.chat.vo.ChatContent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collection;

/**
 * @author li-yuanwen
 */
@Slf4j
@Service
public class ChatServiceImpl implements ChatService {

    @Resource
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

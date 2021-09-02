package com.li.gameremote.modules.chat.facade;

import com.li.gameremote.modules.chat.vo.ChatContent;
import com.li.gamesocket.anno.*;

import java.util.Collection;

/**
 * @author li-yuanwen
 */
@SocketPush
@SocketModule(module = ChatModule.MODULE)
public interface ChatPush {

    /**
     * 消息推送
     * @param pushIds 推送目标
     * @param content 推送消息
     */
    @SocketCommand(command = ChatModule.PUSH)
    void pushMessage(@PushIds Collection<Long> pushIds, @InBody(name = "content") ChatContent content);

}

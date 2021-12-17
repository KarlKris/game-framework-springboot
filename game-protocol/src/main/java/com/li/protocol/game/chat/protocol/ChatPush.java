package com.li.protocol.game.chat.protocol;

import com.li.protocol.game.chat.vo.ChatContent;
import com.li.network.anno.*;

import java.util.Collection;

/**
 * @author li-yuanwen
 */
@SocketPush
@SocketController(module = ChatModule.MODULE)
public interface ChatPush {

    /**
     * 消息推送
     * @param pushIds 推送目标
     * @param content 推送消息
     */
    @SocketMethod(id = ChatModule.PUSH)
    void pushMessage(@PushIds Collection<Long> pushIds, @InBody ChatContent content);

}

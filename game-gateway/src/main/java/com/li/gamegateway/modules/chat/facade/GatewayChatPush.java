package com.li.gamegateway.modules.chat.facade;

import com.li.gamegateway.modules.chat.vo.GatewayChatContent;
import com.li.gamesocket.anno.*;

import java.util.Collection;

/**
 * @author li-yuanwen
 * @date 2021/9/2 21:59
 **/
@SocketPush
@SocketController(module = GatewayChatModule.MODULE)
public interface GatewayChatPush {


    /**
     * 网关服聊天消息推送
     * @param pushIds 推送玩家集
     * @param content 消息内容
     */
    @SocketMethod(id = GatewayChatModule.PUSH_MESSAGE)
    void pushMessage(@PushIds Collection<Long> pushIds, @InBody GatewayChatContent content);

}

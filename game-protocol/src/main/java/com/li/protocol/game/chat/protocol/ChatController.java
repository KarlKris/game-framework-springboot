package com.li.protocol.game.chat.protocol;

import com.li.network.anno.Identity;
import com.li.network.anno.InBody;
import com.li.network.anno.SocketController;
import com.li.network.anno.SocketMethod;

/**
 * @author li-yuanwen
 */
@SocketController(module = ChatModule.MODULE)
public interface ChatController {


    /**
     * 发送消息
     * @param identity 标识
     * @param msg 消息
     */
    @SocketMethod(id = ChatModule.SEND)
    void send(@Identity long identity
            , @InBody String msg);

}

package com.li.gameserver.modules.chat.facade;

import com.li.gameremote.modules.chat.facade.ChatModule;
import com.li.gamesocket.anno.Identity;
import com.li.gamesocket.anno.InBody;
import com.li.gamesocket.anno.SocketMethod;
import com.li.gamesocket.anno.SocketController;

/**
 * @author li-yuanwen
 */
@SocketController(module = ChatModule.MODULE)
public interface ChatFacade {


    /**
     * 发送消息
     * @param identity 标识
     * @param msg 消息
     */
    @SocketMethod(command = ChatModule.SEND)
    void send(@Identity long identity
            , @InBody(name = "msg") String msg);

}

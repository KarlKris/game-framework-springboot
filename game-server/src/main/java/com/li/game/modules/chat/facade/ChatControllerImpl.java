package com.li.game.modules.chat.facade;

import com.li.game.modules.account.service.AccountService;
import com.li.game.modules.chat.service.ChatService;
import com.li.protocol.game.account.vo.AccountVo;
import com.li.protocol.game.chat.protocol.ChatController;
import com.li.protocol.game.chat.vo.ChatContent;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author li-yuanwen
 */
@Component
public class ChatControllerImpl implements ChatController {

    @Resource
    private ChatService chatService;
    @Resource
    private AccountService accountService;

    @Override
    public void send(long identity, String msg) {
        AccountVo accountVo = accountService.transform(identity);
        ChatContent content = ChatContent.of(accountVo, msg);
        chatService.send(identity, content);
    }
}

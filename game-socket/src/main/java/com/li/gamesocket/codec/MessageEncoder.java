package com.li.gamesocket.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author li-yuanwen
 * 自定义协议编码器
 */
@Component
@Slf4j
@ChannelHandler.Sharable
public class MessageEncoder extends MessageToByteEncoder<IMessage> {

    @Override
    protected void encode(ChannelHandlerContext ctx, IMessage msg, ByteBuf out) throws Exception {
        short protocolHeaderIdentity = msg.getProtocolHeaderIdentity();
        if (protocolHeaderIdentity == ProtocolConstant.PROTOCOL_OUTER_HEADER_IDENTITY) {
            ((OuterMessage) msg).writeTo(out);
            int i = out.readableBytes();
            // ByteBuf 长度字段是排在协议头字段之后,即index为2,长度字节为int 即除去长度字段+协议头字段 剩余的就是长度
            out.setInt(2, i - 6);
            return;
        }

        if (protocolHeaderIdentity == ProtocolConstant.PROTOCOL_INNER_HEADER_IDENTITY) {
            ((InnerMessage) msg).writeTo(out);
            int i = out.readableBytes();
            // ByteBuf 长度字段是排在协议头字段之后,即index为2,长度字节为int 即除去长度字段+协议头字段 剩余的就是长度
            out.setInt(2, i - 6);
            return;
        }

        if (log.isDebugEnabled()) {
            log.debug("编码非传输消息类型[{}],协议头[{}],忽略", msg.getClass().getSimpleName(), protocolHeaderIdentity);
        }
    }
}

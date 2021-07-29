package com.li.gamesocket.channelhandler.impl;

import com.li.gamesocket.codec.ProtocolConstant;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author li-yuanwen
 * 通讯双方协议选择
 */
@Slf4j
@Component
@ChannelHandler.Sharable
public class ProtocolSelectorHandler extends ByteToMessageDecoder {

    /** WEBSOCKET 握手数据包头 **/
    public final static short WEBSOCKET_HANDSHAKE_PREFIX = ('G' << 8) + 'E';
    /** 协议头字节数 **/
    public final static short PROTOCOL_BYTES_SIZE = Short.BYTES;

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext
            , ByteBuf byteBuf, List<Object> list) throws Exception {

        // 可读字节数小于协议头字节数,忽略
        if (byteBuf.readByte() < PROTOCOL_BYTES_SIZE) {
            if (log.isDebugEnabled()) {
                log.debug("可读字节数小于协议头字节数[{}],断开连接", PROTOCOL_BYTES_SIZE);
            }

            // 释放ByteBuf
            ReferenceCountUtil.release(byteBuf);
            channelHandlerContext.close();
            return;
        }

        // 读取协议头
        short protocolPrefix = ProtocolConstant.getProtocolHeaderIdentity(byteBuf);
        if (protocolPrefix == WEBSOCKET_HANDSHAKE_PREFIX) {
            // 客户端是websocket连接

        }


        if (protocolPrefix == ProtocolConstant.PROTOCOL_INNER_HEADER_IDENTITY
                || protocolPrefix == ProtocolConstant.PROTOCOL_OUTER_HEADER_IDENTITY) {
            // 自定义协议

        }

        // 不支持的协议,忽略
        if (log.isDebugEnabled()) {
            log.debug("接收到协议头[{}],暂不支持该协议,断开连接", protocolPrefix);
        }

        // 释放ByteBuf
        ReferenceCountUtil.release(byteBuf);
        channelHandlerContext.close();
    }

}

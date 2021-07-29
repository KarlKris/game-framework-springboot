package com.li.gamesocket.codec;

import io.netty.buffer.ByteBuf;

/**
 * @author li-yuanwen
 * 外部自定义协议消息
 */
public class OuterMessage implements IMessage {

    /** 协议头 **/
    private OuterMessageHeader header;
    /** 协议体 **/
    private byte[] body;

    @Override
    public short getProtocolHeaderIdentity() {
        return header.getProtocolId();
    }

    /** 写入至ByteBuf **/
    public void writeTo(ByteBuf out) {
        header.writeTo(out);

        out.writeByte(body.length);
        out.writeBytes(body);
    }

    /** 从ByteBuf中读取 **/
    public static OuterMessage readIn(ByteBuf in) {
        OuterMessage message = new OuterMessage();
        message.header = OuterMessageHeader.readIn(in);
        message.body = new byte[in.readByte()];
        in.readBytes(message.body);
        return message;
    }
}

package com.li.gamesocket.codec;

import io.netty.buffer.ByteBuf;

/**
 * @author li-yuanwen
 * 自定义协议消息
 */
public class InnerMessage implements IMessage {

    /** 消息头 **/
    private InnerMessageHeader header;
    /** 消息体 **/
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
    public static InnerMessage readIn(ByteBuf in) {
        InnerMessage message = new InnerMessage();
        message.header = InnerMessageHeader.readIn(in);
        message.body = new byte[in.readByte()];
        in.readBytes(message.body);
        return message;
    }
}

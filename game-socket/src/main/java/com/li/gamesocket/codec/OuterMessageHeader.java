package com.li.gamesocket.codec;

import com.li.gamesocket.service.Command;
import io.netty.buffer.ByteBuf;
import lombok.Getter;

/**
 * @author li-yuanwen
 * 用于外部自定义协议消息头
 */
@Getter
public class OuterMessageHeader {

    /** 协议标识 **/
    private short protocolId = ProtocolConstant.PROTOCOL_OUTER_HEADER_IDENTITY;
    /** 消息类型 **/
    private byte type;
    /** 请求业务标识 **/
    private Command command;
    /** 消息体压缩标识(true为压缩) **/
    private boolean zip;

    /** 写入至ByteBuf **/
    public void writeTo(ByteBuf out) {
        out.writeShort(protocolId);
        out.writeByte(type);
        command.writeTo(out);
        out.writeBoolean(zip);
    }

    /** 从ByteBuf中读取 **/
    public static OuterMessageHeader readIn(ByteBuf in) {
        OuterMessageHeader header = new OuterMessageHeader();
        header.protocolId = in.readShort();
        header.type = in.readByte();
        header.command = Command.readIn(in);
        header.zip = in.readBoolean();
        return header;
    }

}

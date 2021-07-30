package com.li.gamesocket.protocol;

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
    /** 消息字节长度 **/
    private int length;
    /** 消息类型 **/
    private byte type;
    /** 请求业务标识 **/
    private Command command;
    /** 消息体压缩标识(true为压缩) **/
    private boolean zip;

    /** 写入至ByteBuf **/
    void writeTo(ByteBuf out) {
        out.writeShort(protocolId);
        // 长度占位
        out.writeInt(0);

        // 加入压缩标识
        if (zip) {
            type = ProtocolConstant.addBodyZipState(type);
        }

        out.writeByte(type);

        // 命令标识
        if (ProtocolConstant.hasState(type, ProtocolConstant.COMMAND_MARK)) {
            command.writeTo(out);
        }
    }

    /** 从ByteBuf中读取 **/
    static OuterMessageHeader readIn(ByteBuf in) {
        OuterMessageHeader header = new OuterMessageHeader();
        header.protocolId = in.readShort();
        header.length = in.readInt();
        header.type = in.readByte();

        if (ProtocolConstant.hasState(header.type, ProtocolConstant.COMMAND_MARK)) {
            header.command = Command.readIn(in);
        }

        header.zip = ProtocolConstant.hasState(header.type, ProtocolConstant.BODY_ZIP_MARK);

        return header;
    }


    static OuterMessageHeader of(byte type, Command command, boolean zip) {
        OuterMessageHeader header = new OuterMessageHeader();
        header.type = type;
        header.command = command;
        header.zip = zip;
        return header;
    }

}

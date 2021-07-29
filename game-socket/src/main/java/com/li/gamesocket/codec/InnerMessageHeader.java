package com.li.gamesocket.codec;

import com.li.gamesocket.service.Command;
import io.netty.buffer.ByteBuf;
import lombok.Getter;

/**
 * @author li-yuanwen
 * 服务内部自定义协议消息头
 */
@Getter
public class InnerMessageHeader {

    /** 协议标识 **/
    private short protocolId = ProtocolConstant.PROTOCOL_INNER_HEADER_IDENTITY;
    /** 消息字节长度 **/
    private int length;
    /** 消息类型 **/
    private byte type;
    /** 请求业务标识 **/
    private Command command;
    /** 消息体压缩标识(true为压缩) **/
    private boolean zip;

    /** 消息序号 **/
    private long sn;
    /** 消息来源标识 **/
    private long sourceId;
    /** 消息来源IP地址 **/
    private byte[] ip;


    /** 写入至ByteBuf **/
    public void writeTo(ByteBuf out) {
        out.writeShort(protocolId);
        // 长度占位
        out.writeInt(0);
        out.writeByte(type);
        command.writeTo(out);
        out.writeBoolean(zip);

        out.writeLong(sn);
        out.writeLong(sourceId);
        out.writeByte(ip.length);
        out.writeBytes(ip);
    }

    /** 从ByteBuf中读取 **/
    public static InnerMessageHeader readIn(ByteBuf in) {
        InnerMessageHeader header = new InnerMessageHeader();
        header.protocolId = in.readShort();
        header.length = in.readInt();
        header.type = in.readByte();
        header.command = Command.readIn(in);
        header.zip = in.readBoolean();

        header.sn = in.readLong();
        header.sourceId = in.readLong();
        header.ip = new byte[in.readByte()];
        in.readBytes(header.ip);
        return header;
    }

}

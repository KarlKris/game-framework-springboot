package com.li.gamesocket.protocol;

import cn.hutool.core.util.ArrayUtil;
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

        out.writeLong(sn);
        out.writeLong(sourceId);

        // 判断是否有ip地址
        if (ArrayUtil.isEmpty(ip)) {
            return;
        }

        out.writeByte(ip.length);
        out.writeBytes(ip);
    }

    /** 从ByteBuf中读取 **/
    static InnerMessageHeader readIn(ByteBuf in) {
        InnerMessageHeader header = new InnerMessageHeader();
        header.protocolId = in.readShort();
        header.length = in.readInt();
        header.type = in.readByte();

        if (ProtocolConstant.hasState(header.type, ProtocolConstant.COMMAND_MARK)) {
            header.command = Command.readIn(in);
        }

        header.zip = ProtocolConstant.hasState(header.type, ProtocolConstant.BODY_ZIP_MARK);

        header.sn = in.readLong();
        header.sourceId = in.readLong();

        if (in.readableBytes() > 0) {
            header.ip = new byte[in.readByte()];
            in.readBytes(header.ip);
        }

        return header;
    }

    static InnerMessageHeader of(byte msgType, Command command
            , boolean zip, long sn, long sourceId, byte[] ip) {
        InnerMessageHeader header = new InnerMessageHeader();
        header.type = msgType;
        header.command = command;
        header.zip = zip;
        header.sn = sn;
        header.sourceId = sourceId;
        header.ip = ip;
        return header;
    }

}

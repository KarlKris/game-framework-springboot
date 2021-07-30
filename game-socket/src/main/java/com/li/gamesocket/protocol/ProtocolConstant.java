package com.li.gamesocket.protocol;

import io.netty.buffer.ByteBuf;

/**
 * @author li-yuanwen
 * 协议常量
 */
public interface ProtocolConstant {

    /** 内部协议头标识 **/
    short PROTOCOL_INNER_HEADER_IDENTITY = 0x24;
    /** 外部协议头标识 **/
    short PROTOCOL_OUTER_HEADER_IDENTITY = 0x08;

    /**
     * 读取协议头标识
     * @param in ByteBuf
     * @return /
     */
    static short getProtocolHeaderIdentity(ByteBuf in) {
        // 标记读位置
        in.markReaderIndex();
        short protocolHeaderIdentity = in.readShort();
        in.resetReaderIndex();
        return protocolHeaderIdentity;
    }

    // ------------- 消息类型 -------------------------------

    /**
     * 消息类型对应于消息头#type(范围-128-127) 取1->127 即最高位符号均取0
     * 8个字节=0 + 1位是否携带命令(0不携带命令,1携带命令) + 1位(消息体是否压缩 0未压缩 1已压缩) + 5位消息类型
     */

    /** 携带命令 0 1 0 00000 **/
    byte COMMAND_MARK = 0x40;

    /** 消息压缩 0 0 1 00000 **/
    byte BODY_ZIP_MARK = 0x20;

    // 具体消息类型  新增消息类型时必须确认是否会携带命令

    /** 心跳检测请求(不携带命令) 0 0 0 00001 **/
    byte HEART_BEAT_REQ = 0x1;

    /** 心跳检测响应(不携带命令) 0 0 0 00010 **/
    byte HEART_BEAT_RES = 0x2;

    /** 业务请求(携带命令) 0 1 0 00011 **/
    byte VOCATIONAL_WORK_REQ = 0x43;

    /** 业务响应(携带命令) 0 1 0 00100 **/
    byte VOCATIONAL_WORK_RES = 0x44;


    /**
     * 加上消息压缩标识
     * @param type 消息类型
     * @return /
     */
    static byte addBodyZipState(byte type) {
        return type |= BODY_ZIP_MARK;
    }

    /**
     * 消息类型字段是否含有某种标识
     * @param type 消息类型
     * @param mark 标识掩码
     * @return /
     */
    static boolean hasState(byte type, byte mark) {
        return ( type &= mark ) > 0;
    }

}

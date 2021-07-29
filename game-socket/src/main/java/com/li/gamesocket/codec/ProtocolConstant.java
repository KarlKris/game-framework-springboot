package com.li.gamesocket.codec;

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

}

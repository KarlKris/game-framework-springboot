package com.li.network.serialize;

/**
 * 序列化/反序列化类型
 * @author li-yuanwen
 */
public enum SerializeType {

    /** Proto Stuff框架 **/
    PROTOBUF((byte)0x0),

    /** JSON **/
    JSON((byte)0x1),

    ;

    /** 类型标识,2位表示 即0-3 **/
    private byte type;

    SerializeType(byte type) {
        this.type = type;
    }

    public byte getType() {
        return this.type;
    }

}

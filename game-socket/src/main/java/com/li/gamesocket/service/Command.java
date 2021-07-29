package com.li.gamesocket.service;

import io.netty.buffer.ByteBuf;

/**
 * @author li-yuanwen
 * 封装请求业务标识
 */
public class Command {

    /** 消息所属模块号 **/
    private short module;
    /** 消息所属命令号 **/
    private byte command;

    public Command(short module, byte command) {
        this.module = module;
        this.command = command;
    }

    /** 写入至ByteBuf **/
    public void writeTo(ByteBuf out) {
        out.writeShort(module);
        out.writeByte(command);
    }

    /** 从ByteBuf中读取 **/
    public static Command readIn(ByteBuf in) {
        return new Command(in.readShort(), in.readByte());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Command)) {
            return false;
        }

        Command command1 = (Command) o;

        if (module != command1.module) {
            return false;
        }
        return command == command1.command;
    }

    @Override
    public int hashCode() {
        int result = module;
        result = 31 * result + (int) command;
        return result;
    }
}

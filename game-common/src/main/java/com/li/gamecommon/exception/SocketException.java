package com.li.gamecommon.exception;

import lombok.Getter;

/**
 * @author li-yuanwen
 * @date 2021/8/4 22:24
 * 服务器逻辑异常基础类
 **/
@Getter
public class SocketException extends RuntimeException {

    /**
     * 错误码
     **/
    private int errorCode;

    public SocketException(int errorCode) {
        super();
        this.errorCode = errorCode;
    }

    public SocketException(int errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public SocketException(int errorCode, String message, Throwable e) {
        super(message, e);
        this.errorCode = errorCode;
    }
}

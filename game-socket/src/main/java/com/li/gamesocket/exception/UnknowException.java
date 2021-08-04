package com.li.gamesocket.exception;

/**
 * @author li-yuanwen
 * @date 2021/8/4 22:24
 * 服务器逻辑异常基础类
 **/
public class UnknowException extends RuntimeException {

    /** 错误码 **/
    private int code;

    public UnknowException(int code) {
        super();
        this.code = code;
    }

    public UnknowException(int code, String message) {
        super(message);
        this.code = code;
    }

    public UnknowException(int code, String message, Throwable e) {
        super(message, e);
        this.code = code;
    }
}

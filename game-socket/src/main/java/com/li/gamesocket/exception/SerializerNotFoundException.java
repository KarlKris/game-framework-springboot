package com.li.gamesocket.exception;

/**
 * @author li-yuanwen
 * @date 2021/7/31 17:56
 * 找不到对应的序列化工具
 **/
public class SerializerNotFoundException extends RuntimeException {

    public SerializerNotFoundException() {
        super();
    }

    public SerializerNotFoundException(String message) {
        super(message);
    }

    public SerializerNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public SerializerNotFoundException(Throwable cause) {
        super(cause);
    }

    protected SerializerNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

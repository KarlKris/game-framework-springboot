package com.li.gamesocket.exception;

/**
 * @author li-yuanwen
 * @date 2021/7/31 18:50
 * 序列化或反序列化失败异常
 **/
public class SerializeFailException extends RuntimeException {

    public SerializeFailException() {
        super();
    }

    public SerializeFailException(String message) {
        super(message);
    }

    public SerializeFailException(String message, Throwable cause) {
        super(message, cause);
    }

    public SerializeFailException(Throwable cause) {
        super(cause);
    }

    protected SerializeFailException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

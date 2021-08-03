package com.li.gamesocket.exception;

import lombok.Getter;

/**
 * @author li-yuanwen
 * 业务方法参数解析异常
 */
@Getter
public class MethodParamAnalysisException extends RuntimeException {

    public MethodParamAnalysisException() {
        super();
    }

    public MethodParamAnalysisException(String message) {
        super(message);
    }

    public MethodParamAnalysisException(String message, Throwable cause) {
        super(message, cause);
    }

    public MethodParamAnalysisException(Throwable cause) {
        super(cause);
    }

    protected MethodParamAnalysisException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

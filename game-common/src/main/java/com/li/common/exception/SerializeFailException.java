package com.li.common.exception;


import com.li.common.exception.code.ServerErrorCode;

/**
 * @author li-yuanwen
 * @date 2021/7/31 18:50
 * 序列化或反序列化失败异常
 **/
public class SerializeFailException extends SocketException {


    public SerializeFailException(String message, Throwable cause) {
        super(ServerErrorCode.SERIALIZE_FAIL, message, cause);
    }

}

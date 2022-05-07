package com.li.network.protocol;

import com.li.network.message.SocketProtocol;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.lang.reflect.Method;

/**
 * @author li-yuanwen
 * 方法上下文
 */
@Getter
@AllArgsConstructor
public class ProtocolMethodCtx {

    /** 命令 **/
    private SocketProtocol protocol;
    /** 具体方法 **/
    private Method method;
    /** 方法参数 **/
    private MethodParameter[] params;


    /** 方法是否需要身份标识 **/
    boolean identity() {
        for (MethodParameter parameter : params) {
            if (parameter instanceof IdentityMethodParameter) {
                return true;
            }
        }
        return false;
    }
}

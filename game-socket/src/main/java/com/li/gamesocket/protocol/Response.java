package com.li.gamesocket.protocol;

/**
 * @author li-yuanwen
 * 消息响应中消息体封装
 */
public class Response {

    /** 请求响应成功码 **/
    public static final byte SUCCESS = 0x1;

    /** 状态码 **/
    private byte code;
    /** 响应内容 **/
    private Object content;

}

package com.li.gamesocket.protocol;

/**
 * @author li-yuanwen
 * 消息响应中消息体封装
 */
public class Response {

    /** 默认成功响应 **/
    public static final Response DEFAULT_SUCCESS = SUCCESS(null);
    /** 序列化/反序列化失败 **/
    public static final Response SERIALIZE_FAIL = ERROR(ResultCode.SERIALIZE_FAIL);
    /** 身份未认定 **/
    public static final Response NO_IDENTITY = ERROR(ResultCode.NO_IDENTITY);
    /** 默认失败 **/
    public static final Response CONVERT_FAIL = ERROR(ResultCode.CONVERT_FAIL);


    /** 状态码 **/
    private int code;
    /** 响应内容 **/
    private Object content;

    public static Response SUCCESS(Object content) {
        Response response = new Response();
        response.code = ResultCode.SUCCESS;
        response.content = content;
        return response;
    }

    public static Response ERROR(int error) {
        Response response = new Response();
        response.code = error;
        return response;
    }

}

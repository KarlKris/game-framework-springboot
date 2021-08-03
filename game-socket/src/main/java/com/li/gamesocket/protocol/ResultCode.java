package com.li.gamesocket.protocol;

/**
 * @author li-yuanwen
 * @date 2021/7/31 18:57
 * 响应体消息状态码
 **/
public interface ResultCode {

    /** 请求响应成功码  **/
    int SUCCESS = 0;

    /** 默认失败,不对客户端展示具体原因 **/
    int FAIL = -1;

    /** 无身份标识 **/
    int NO_IDENTITY = -2;

    /** 序列化消息体失败 **/
    int SERIALIZE_FAIL = -3;

    /** 方法参数类型转换失败 **/
    int CONVERT_FAIL = -4;

    /** 方法参数解析异常 **/
    int PARAM_ANALYSIS_ERROR = -5;


}

package com.li.gamesocket.protocol;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * @author li-yuanwen
 * @date 2021/7/31 12:24
 * 消息请求中消息体的封装
 **/
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Request {

    /** 请求业务所需数据 **/
    private Map<String, Object> params;

}

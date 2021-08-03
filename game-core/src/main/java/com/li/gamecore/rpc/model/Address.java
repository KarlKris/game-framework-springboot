package com.li.gamecore.rpc.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author li-yuanwen
 * @date 2021/8/3 22:30
 * IP地址
 **/
@Getter
@AllArgsConstructor
public class Address {

    /** Ip地址 **/
    private String ip;
    /** 端口 **/
    private int port;

}

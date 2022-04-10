package com.li.manager.common.security.config;

import lombok.Data;

/**
 * 登录验证码配置信息
 * @author li-yuanwen
 * @date 2021/8/28 14:20
 **/
@Data
public class LoginCode {

    /** 验证码配置 **/
    private LoginCodeEnum codeType;

    /** 验证码有效期 分钟 **/
    private long expiration = 2L;

    /** 验证码内容长度 **/
    private int length = 2;

    /** 验证码宽度 **/
    private int width = 111;

    /** 验证码高度 **/
    private int height = 36;

    /** 验证码字体 **/
    private String fontName;

    /** 字体大小 **/
    private int fontSize = 25;
}

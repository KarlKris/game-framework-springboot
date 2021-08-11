package com.li.gamegateway.modules.login.facade;

/**
 * @author li-yuanwen
 * 网关登录异常码
 */
public interface GatewayLoginResultCode {

    /** 验签失败 **/
    int SIGN_ERROR = 1001;

    /** 账号不可为空 **/
    int ACCOUNT_INVALID = 1002;

}

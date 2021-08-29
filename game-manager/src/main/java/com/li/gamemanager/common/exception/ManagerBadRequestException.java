package com.li.gamemanager.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

/**
 * 管理后台异常
 * @author li-yuanwen
 * @date 2021/8/28 23:44
 **/
@Getter
public class ManagerBadRequestException extends RuntimeException{

    /** 状态码 **/
    private int status = BAD_REQUEST.value();

    public ManagerBadRequestException(String msg){
        super(msg);
    }

    public ManagerBadRequestException(HttpStatus status, String msg){
        super(msg);
        this.status = status.value();
    }

}

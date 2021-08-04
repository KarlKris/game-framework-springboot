package com.li.gamesocket.exception;

import com.li.gamesocket.protocol.ResultCode;
import lombok.Getter;

/**
 * @author li-yuanwen
 * 业务方法参数解析异常
 */
@Getter
public class MethodParamAnalysisException extends UnknowException {

    public MethodParamAnalysisException(String message) {
        super(ResultCode.PARAM_ANALYSIS_ERROR, message);
    }

}

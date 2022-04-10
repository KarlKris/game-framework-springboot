package com.li.common.utils;

import com.li.common.logging.message.RecordMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 通用日志工具类
 * @author li-yuanwen
 * @date 2022/3/29
 */
public class LogUtil {

    /**
     * 记录埋点日志
     * @param loggerName logger名
     * @param recordMessage 日志
     */
    public static void log(String loggerName, RecordMessage recordMessage) {
        Logger logger = LogManager.getLogger(loggerName);
        logger.trace(recordMessage);
    }

}

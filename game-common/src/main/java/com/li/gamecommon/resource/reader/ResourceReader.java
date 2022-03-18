package com.li.gamecommon.resource.reader;

import java.io.InputStream;
import java.util.List;

/**
 * 文件资源读取器
 * @author li-yuanwen
 * @date 2022/3/17
 */
public interface ResourceReader {

    /**
     * 读取的文件后缀名
     * @return 文件后缀
     */
    String getFileSuffix();

    /**
     * 资源读取
     * @param in 资源Input
     * @param clz 目标类型
     * @param <E> 实际类型
     * @return 资源集
     */
    <E> List<E> read(InputStream in, Class<E> clz);

}

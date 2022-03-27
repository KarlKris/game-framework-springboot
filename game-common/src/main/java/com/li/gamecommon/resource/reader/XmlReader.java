package com.li.gamecommon.resource.reader;

import cn.hutool.core.util.XmlUtil;
import org.w3c.dom.Document;

import java.io.InputStream;
import java.util.List;

/**
 * todo xml文件读取器
 * @author li-yuanwen
 * @date 2022/3/23
 */
public class XmlReader implements ResourceReader {

    @Override
    public String getFileSuffix() {
        return "xml";
    }

    @Override
    public <E> List<E> read(InputStream in, Class<E> clz) {
        Document document = XmlUtil.readXML(in);
        return null;
    }
}

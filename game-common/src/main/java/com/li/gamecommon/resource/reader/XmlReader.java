package com.li.gamecommon.resource.reader;

import com.li.gamecommon.resource.convertor.StrConvertorHolder;
import com.li.gamecommon.utils.ObjectsUtil;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import javax.annotation.Resource;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.LinkedList;
import java.util.List;

/**
 * xml文件读取器 后续可转为使用DOM4J框架读取XML
 * @author li-yuanwen
 * @date 2022/3/23
 */
@Component
public class XmlReader implements ResourceReader {

    private final SAXReader reader = new SAXReader();

    /** 数据标签 **/
    private static final String ELEMENT_NAME = "item";

    @Resource
    private StrConvertorHolder strConvertorHolder;

    @Override
    public String getFileSuffix() {
        return "xml";
    }

    @Override
    public <E> List<E> read(InputStream in, Class<E> clz) {
        FieldParser fieldParser = new FieldParser(clz);

        List<E> results = new LinkedList<>();
        for (Element element : getElements(in)) {
            E instance = ObjectsUtil.newInstance(clz);
            for (XmlFieldHolder fieldHolder : fieldParser.fieldHolders) {
                String fieldName = fieldHolder.getFieldName();
                Attribute attribute = element.attribute(fieldName);
                if (attribute == null) {
                    continue;
                }
                fieldHolder.inject(instance, attribute.getValue());
            }
            results.add(instance);
        }
        return results;
    }

    private List<Element> getElements(InputStream in) {
        try {
            Document document = reader.read(in);
            return (List<Element>) document.getRootElement().elements(ELEMENT_NAME);
        } catch (DocumentException e) {
            throw new RuntimeException("读取xml资源异常", e);
        }
    }

    private final class FieldParser {

        private final List<XmlFieldHolder> fieldHolders;

        FieldParser(Class<?> clz) {
            final List<XmlFieldHolder> fieldHolders = new LinkedList<>();
            ReflectionUtils.doWithFields(clz, field -> {
                fieldHolders.add(new XmlFieldHolder(field));
            }, field -> !Modifier.isStatic(field.getModifiers()));
            this.fieldHolders = fieldHolders;
        }

    }

    private final class XmlFieldHolder extends AbstractFieldHolder {
        XmlFieldHolder(Field field) {
            super(field, convertorType -> strConvertorHolder.getStrConvertorByType(convertorType));
        }
    }
}

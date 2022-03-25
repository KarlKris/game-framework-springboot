package com.li.gamecommon.resource.core;

import com.li.gamecommon.resource.anno.ResourceForeignKey;
import lombok.Getter;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;

/**
 * 资源定义信息
 * @author li-yuanwen
 * @date 2022/3/16
 */
@Getter
public class ResourceDefinition {

    /** 资源对象 **/
    private final Class<?> clz;
    /** 资源根路径 **/
    private final String rootPath;
    /** 是否有外键 **/
    private final List<Field> foreignKeyFields;

    public ResourceDefinition(Class<?> clz, String rootPath) {
        this.clz = clz;
        this.rootPath = rootPath;
        this.foreignKeyFields = new LinkedList<>();
        ReflectionUtils.doWithFields(clz, field -> {
            if (field.getAnnotation(ResourceForeignKey.class) == null) {
                return;
            }
            foreignKeyFields.add(field);
        });
    }

    public boolean haveForeignKey() {
        return !foreignKeyFields.isEmpty();
    }
}

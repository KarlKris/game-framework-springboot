package com.li.battle.util;

import com.li.battle.core.Attribute;

/**
 * 属性值变更工具类
 * @author li-yuanwen
 * @date 2022/5/23
 */
public class AttributeValueAlter extends ValueAlter {

    /** 属性类型 **/
    private final Attribute attribute;

    public AttributeValueAlter(Attribute attribute, long alterValue) {
        super(alterValue);
        this.attribute = attribute;
    }

    public Attribute getAttribute() {
        return attribute;
    }

    public static AttributeValueAlter merge(AttributeValueAlter a1, AttributeValueAlter a2) {
        if (a1.attribute != a2.attribute) {
            throw new RuntimeException("AttributeValueAlter合并不同属性");
        }
        return new AttributeValueAlter(a1.attribute, a1.getValue() + a2.getValue());
    }


}

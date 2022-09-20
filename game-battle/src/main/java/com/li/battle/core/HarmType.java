package com.li.battle.core;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 伤害类型
 * @author li-yuanwen
 * @date 2022/9/15
 */
public enum HarmType {

    /** 物理伤害 **/
    PHYSICS(1),

    /** 法术伤害 **/
    MAGIC(2),

    ;

    /** code值 **/
    private final int code;

    HarmType(int code) {
        this.code = code;
    }

    @JsonValue
    public int getCode() {
        return code;
    }

    @JsonCreator
    public static HarmType from(int code) {
        for (HarmType type : HarmType.values()) {
            if (type.getCode() == code) {
                return type;
            }
        }
        return null;
    }

    public static void main(String[] args) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(HarmType.MAGIC);
        System.out.println(json);
        HarmType type = objectMapper.readValue(json, HarmType.class);
        System.out.println(type.name());
    }
}

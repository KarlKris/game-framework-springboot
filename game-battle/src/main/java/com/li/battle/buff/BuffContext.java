package com.li.battle.buff;

import lombok.*;

/**
 * Buff创建时候的一些上下文数据，它是一个不确定的项，通过外部传入各种自定义的数据，然后在Buff逻辑中使用这些自定义数据。
 * @author li-yuanwen
 * @date 2022/9/24
 */
@Data
public class BuffContext {

    /** 护盾增加值 **/
    private int shieldValue;


}
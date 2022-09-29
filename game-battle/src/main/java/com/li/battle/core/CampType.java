package com.li.battle.core;

/**
 * 阵营类型,共分3类,进攻方,防守方,中立方
 * @author li-yuanwen
 * @date 2022/5/28
 */
public enum CampType {

    // 后续增加阵营时,需要同步修改SelectorType.ENEMY_CAMP选择器逻辑

    /** 进攻方 **/
    ATTACKER,

    /** 防守方 **/
    DEFENDER,

    /** 中立方 **/
    NEUTRAL,

    ;


    public CampType getEnemyType() {
       if (this == CampType.ATTACKER) {
           return CampType.DEFENDER;
       } else if (this == CampType.DEFENDER) {
           return CampType.ATTACKER;
       } else {
           return null;
       }
    }

}

package com.li.gamecore.dao.model;

import com.li.gamecore.dao.IEntity;
import lombok.Getter;

/**
 * @author li-yuanwen
 * 持久化元素抽象
 */
@Getter
public class PersistElement {

    /** 持久化类型 **/
    private final PersistType type;
    /** 持久化对象 **/
    private final IEntity entity;
    /** 类名 **/
    private final String entityName;

    public PersistElement(PersistType type, IEntity entity) {
        this.type = type;
        this.entity = entity;
        this.entityName = entity.getClass().getName();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PersistElement)) {
            return false;
        }

        PersistElement that = (PersistElement) o;

        if (type != that.type) {
            return false;
        }
        if (!entity.getId().equals(that.entity.getId())) {
            return false;
        }
        return entityName.equals(that.entityName);
    }

    @Override
    public int hashCode() {
        int result = type.hashCode();
        result = 31 * result + entity.getId().hashCode();
        result = 31 * result + entityName.hashCode();
        return result;
    }
}

package com.li.common.utils.queue;

import java.util.*;

/**
 * PriorityQueue的默认实现
 * @author li-yuanwen
 * @date 2022/10/13
 */
public class DefaultPriorityQueue<T extends PriorityQueueNode> extends AbstractQueue<T> implements PriorityQueue<T>  {

    @Override
    public void priorityChanged(T node) {

    }

    @Override
    public Iterator<T> iterator() {
        return null;
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public boolean offer(T t) {
        return false;
    }

    @Override
    public T poll() {
        return null;
    }

    @Override
    public T peek() {
        return null;
    }
}

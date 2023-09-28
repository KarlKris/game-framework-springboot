package com.li.common.utils;

import cn.hutool.core.util.RandomUtil;
import com.li.common.utils.queue.*;
import com.li.common.utils.queue.PriorityQueue;
import lombok.Getter;

import java.util.*;

/**
 * DefaultPriorityQueue单元测试
 * @author li-yuanwen
 * @date 2022/10/14
 */
public class DefaultPriorityQueueTest {

    public static void main(String[] args) {

        Element changeElement = new Element(8);

        PriorityQueue<Element> queue = new DefaultPriorityQueue<>(Comparator.comparingInt(Element::getE));
        queue.offer(changeElement);

        addElement(queue, 50);

        System.out.println("-----------------------------测试变更--------------------------------");

        // 测试变更
        changeElement.setE(24);
        // 测试遍历
        Iterator<Element> iterator = queue.iterator();
        while (iterator.hasNext()) {
            Element element = iterator.next();
            System.out.println(element);
        }

        System.out.println("-----------------------------测试遍历删除--------------------------------");

        // 测试遍历删除
        iterator = queue.iterator();
        while (iterator.hasNext()) {
            Element element = iterator.next();
            if (element.e == 24) {
                iterator.remove();
                continue;
            }
            System.out.println(element);
        }

        System.out.println("--------------------------测试最小堆性质-----------------------------------");

        // 测试最小堆性质
        int size = queue.size();
        for (int i = 0; i < size; i++) {
            System.out.println(queue.poll());
        }



    }


    public static void addElement(PriorityQueue<Element> queue, int num) {
        Set<Integer> set = new HashSet<>();
        set.add(8);
        set.add(24);

        int i = 0;
        while (i < num) {
            int r = RandomUtil.randomInt(0, 1000);
            if (set.add(r)) {
                i++;
                queue.offer(new Element(r));
            }
        }
    }


    @Getter
    private static final class Element implements PriorityQueueNode {

        DefaultPriorityQueue<Element> queue;
        private int index = PriorityQueueNode.INDEX_NOT_IN_QUEUE;
        private int e;

        public Element(int e) {
            this.e = e;
        }

        @Override
        public int priorityQueueIndex(DefaultPriorityQueue<?> queue) {
            return index;
        }

        @SuppressWarnings("unchecked")
        @Override
        public void priorityQueueIndex(DefaultPriorityQueue<?> queue, int index) {
            this.queue =  (DefaultPriorityQueue<Element>) queue;
            this.index = index;
        }

        public void setE(int e) {
            this.e = e;
            if (queue != null) {
                queue.priorityChanged(this);
            }

        }

        @Override
        public String toString() {
            return "Element{" +
                    "index=" + index +
                    ", e=" + e +
                    '}';
        }
    }

}

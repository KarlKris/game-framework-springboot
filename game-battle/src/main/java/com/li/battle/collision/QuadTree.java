package com.li.battle.collision;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * 四叉树,优化碰撞检测列表
 * @author li-yuanwen
 * @date 2022/5/31
 */
public class QuadTree<T extends Shape> {

    /** 一个节点所能持有的最大对象数量，如果超过则进行分裂 **/
    private static final int MAX_OBJECTS = 10;
    /** 子节点的最大深度 **/
    private static final int MAX_LEVELS = 5;

    private final int level;
    private final List<T> objects;
    private final Rectangle2D bounds;
    private final QuadTree<T>[] nodes;

    public QuadTree(int pLevel, Rectangle2D pBounds) {
        level = pLevel;
        objects = new ArrayList<>();
        bounds = pBounds;
        nodes = (QuadTree<T>[]) new QuadTree[4];
    }

    public void clear() {
        objects.clear();
        clearSubNode();
    }

    private void clearSubNode() {
        for (int i = 0; i < nodes.length; i++) {
            if (nodes[i] != null) {
                nodes[i].clear();
                nodes[i] = null;
            }
        }
    }


    private int getSubSize() {
        int size = objects.size();
        for (int i = 0; i < nodes.length; i++) {
            if (nodes[i] != null) {
                size += nodes[i].getSubSize();
            }
        }
        return size;
    }

    /**
     * Splits the node into 4 subnodes
     */
    private void split() {
        int subWidth = (int) (bounds.getWidth() / 2);
        int subHeight = (int) (bounds.getHeight() / 2);
        int x = (int) bounds.getX();
        int y = (int) bounds.getY();

        // 均分4等份,从右下角开始,编号为0
        nodes[0] = new QuadTree<T>(level + 1, new Rectangle2D(x + subWidth, y, subWidth, subHeight));
        nodes[1] = new QuadTree<T>(level + 1, new Rectangle2D(x, y, subWidth, subHeight));
        nodes[2] = new QuadTree<T>(level + 1, new Rectangle2D(x, y + subHeight, subWidth, subHeight));
        nodes[3] = new QuadTree<T>(level + 1, new Rectangle2D(x + subWidth, y + subHeight, subWidth, subHeight));
    }

    private List<T> getSubNodes() {
        List<T> subNodes = getSubNodes0();
        subNodes.removeIf(objects::contains);
        return subNodes;
    }

    private List<T> getSubNodes0() {
        List<T> list = new LinkedList<>(objects);
        for (int i = 0; i < nodes.length; i++) {
            if (nodes[i] != null) {
                list.addAll(nodes[i].getSubNodes0());
            }
        }

        return list;
    }


    private void merge() {
        objects.addAll(getSubNodes());
        clearSubNode();
    }


    /**
     * Determine which node the object belongs to. -1 means
     * object cannot completely fit within a child node and is part
     * of the parent node
     * @param shape 指定区域
     * @return 区域编号
     */
    private int getIndex(Shape shape) {
        int index = -1;
        double verticalMidpoint = bounds.getX() + (bounds.getWidth() / 2);
        double horizontalMidpoint = bounds.getY() + (bounds.getHeight() / 2);

        // Object can completely fit within the top quadrants
        boolean topQuadrant = (shape.getBottom() < horizontalMidpoint && shape.getTop() < horizontalMidpoint);
        // Object can completely fit within the bottom quadrants
        boolean bottomQuadrant = (shape.getBottom() > horizontalMidpoint);

        // Object can completely fit within the left quadrants
        if (shape.getLeft() < verticalMidpoint && shape.getRight() < verticalMidpoint) {
            if (topQuadrant) {
                index = 1;
            }
            else if (bottomQuadrant) {
                index = 2;
            }
        }
        // Object can completely fit within the right quadrants
        else if (shape.getLeft() > verticalMidpoint) {
            if (topQuadrant) {
                index = 0;
            }
            else if (bottomQuadrant) {
                index = 3;
            }
        }

        return index;
    }

    /**
     * Insert the object into the quadtree. If the node
     * exceeds the capacity, it will split and add all
     * objects to their corresponding nodes.
     * @param t 插入目标
     */
    public void insert(T t) {
        if (nodes[0] != null) {
            int index = getIndex(t);
            // 任何不能完全适合子节点内部边界规则的对象将会被放置在父节点中。
            if (index != -1) {
                nodes[index].insert(t);

                return;
            }
        }

        objects.add(t);

        if (objects.size() > MAX_OBJECTS && level < MAX_LEVELS) {
            if (nodes[0] == null) {
                split();
            }

            int i = 0;
            while (i < objects.size()) {
                int index = getIndex(objects.get(i));
                if (index != -1) {
                    nodes[index].insert(objects.remove(i));
                }
                else {
                    i++;
                }
            }
        }
    }

    public void remove(T t) {
        boolean remove = objects.remove(t);
        if (remove) {
            return;
        }

        int index = getIndex(t);
        if (index == -1 || nodes[index] == null) {
            return;
        }

        nodes[index].remove(t);
        int subSize = getSubSize();
        if (subSize <= MAX_OBJECTS) {
            merge();
        }
    }


    /**
     * Return all objects that could collide with the given object
     * @param shape 形状
     * @return 形状可能会产生碰撞的所有对象
     */
    public List<T> retrieve(Shape shape) {
        List<T> returnObjects = new LinkedList<>();
        int index = getIndex(shape);
        if (index != -1 && nodes[0] != null) {
            returnObjects.addAll(nodes[index].retrieve(shape));
        }

        returnObjects.addAll(objects);

        return returnObjects;
    }

}

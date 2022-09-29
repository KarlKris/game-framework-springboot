package com.li.battle.core.map;

import cn.hutool.core.lang.Pair;
import com.li.battle.resource.MapConfig;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

import java.util.*;

/**
 * 基于格子的地图实现
 * @author li-yuanwen
 * @date 2022/6/6
 */
public class DefaultSceneMap implements SceneMap {

    /** 地图标识 **/
    private final int mapId;
    /** 地图长度 **/
    private final int horizontalLength;
    /** 地图宽度 **/
    private final int verticalLength;
    /** 格子长度 **/
    private final int gridSize;
    /** 可走格子 **/
    private final Map<String, Grid> grids;

    public DefaultSceneMap(MapConfig config) {
        this.mapId = config.getId();
        this.horizontalLength = config.getHorizontalLength();
        this.verticalLength = config.getVerticalLength();
        this.gridSize = config.getGridSize();
        this.grids = new HashMap<>(config.getGrids().size());
        for (Point point : config.getGrids()) {
            Grid grid = new Grid(point.getX(), point.getY(), gridSize);
            grids.put(grid.id(), grid);
        }
    }

    @Override
    public int getMapId() {
        return mapId;
    }

    @Override
    public int getGridSize() {
        return gridSize;
    }

    @Override
    public int getHorizontalLength() {
        return horizontalLength;
    }

    @Override
    public int getVerticalLength() {
        return verticalLength;
    }

    @Override
    public boolean isExistGrid(double x, double y) {
        return getGrid(x, y) != null;
    }

    @Override
    public Grid getGrid(double x, double y) {
        Pair<Integer, Integer> pair = translate(x, y);
        return grids.get(Grid.toId(pair.getKey(), pair.getValue()));
    }

    @Override
    public Grid getGridByGridPoint(int gridX, int gridY) {
        return grids.get(Grid.toId(gridX, gridY));
    }

    @Override
    public List<Vector2D> findWayByAStar(double fromX, double fromY, double toX, double toY) {
        Grid fromGrid = getGrid(fromX, fromY);
        if (fromGrid == null) {
            return Collections.emptyList();
        }
        Grid toGrid = getGrid(toX, toY);
        if (toGrid == null) {
            return Collections.emptyList();
        }
        // 同一个格子内,直接到达
        if (fromGrid == toGrid) {
            return Collections.singletonList(new Vector2D(toX, toY));
        }

        // A*寻路算法
        // A*算法在运算过程中，每次从优先队列中选取f(n)值最小（优先级最高）的节点作为下一个待遍历的节点。
        PriorityQueue<Node> queue = new PriorityQueue<>(Comparator.comparingDouble(o -> o.priority));
        queue.offer(new Node(fromGrid, fromGrid, toGrid));

        // 已遍历格子集合
        Set<Grid> closeSet = new HashSet<>();

        Node node;
        while((node = queue.poll()) != null) {
            // 找到终点
            if (node.grid == toGrid) {
                break;
            }
            // 加入到已检查集合
            closeSet.add(node.grid);
            // 遍历附近节点
            for (Direction direction : Direction.values()) {
                Grid nextGrid = direction.calculateNextGrid(this, node.grid);
                if (nextGrid == null) {
                    // 该方向的相邻格子不存在
                    continue;
                }
                // 如果邻近节点m在close_set中，则
                if (closeSet.contains(nextGrid)) {
                    continue;
                }
                // 如果邻近节点m也不在队列中
                Optional<Node> optional = queue.stream().filter(n -> n.grid.equals(nextGrid)).findAny();
                if (!optional.isPresent()) {
                    queue.offer(new Node(node, nextGrid, fromGrid, toGrid));
                }
            }
        }

        // 最终路径
        List<Vector2D> ways = new LinkedList<>();
        while (node != null) {
            if (node.grid.equals(toGrid)) {
                ways.add(new Vector2D(toX, toY));
            } else if (node.grid.equals(fromGrid)){
                ways.add(new Vector2D(fromX, fromY));
            } else {
                // todo 优化 移除拐点
                ways.add(new Vector2D(node.grid.getX(), node.grid.getY()));
            }
            node = node.parent;
        }

        Collections.reverse(ways);
        return ways;
    }

    /**
     * 将坐标点转换为格子点
     * @param x x坐标点
     * @param y y坐标点
     * @return 格子坐标
     */
    private Pair<Integer, Integer> translate(double x, double y) {
        int intX = (int) x;
        int intY = (int) y;
        int remainderX = intX % gridSize;
        int remainderY = intY % gridSize;
        return new Pair<>(intX - remainderX, intY - remainderY);
    }

    /**
     * A*算法中的链表
     */
    private static final class Node {

        /** 父节点 **/
        private final Node parent;
        /** 格子 **/
        private final Grid grid;
        /** 优先级 **/
        private final double priority;

        Node(Grid grid, Grid fromGrid, Grid toGrid) {
            this(null, grid, fromGrid, toGrid);
        }

        Node(Node parent, Grid grid, Grid fromGrid, Grid toGrid) {
            this.parent = parent;
            this.grid = grid;
            this.priority = calculatePriority(fromGrid, toGrid);
        }

        /**
         * 计算格子优先级 f(n) = g(n) + h(n)
         * @param toGrid 寻路目标
         * @return 优先级
         */
        private double calculatePriority(Grid fromGrid, Grid toGrid) {
            // g(n) 是节点n距离起点的代价
            // h(n) 是节点n距离终点的预计代价
            // 均采用欧几里得距离计算代价
            return calculateCost(fromGrid) + calculateCost(toGrid);
        }

        private double calculateCost(Grid grid) {
            if (grid == this.grid) {
                return 0;
            }
            // 允许向任意方法移动,代价使用欧几里得距离
            int dx = Math.abs(grid.getX() - this.grid.getX());
            int dy = Math.abs(grid.getY() - this.grid.getY());
            // 代价D是指两个相邻节点之间的移动代价 D==1
            return Math.sqrt(dx * dx + dy * dy);
        }



    }
    

}

package com.li.common.concurrency;

import cn.hutool.core.thread.NamedThreadFactory;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 业务线程池实现类
 * @author li-yuanwen
 * @date 2022/2/8
 */
public class GenericSerializedExecutorService implements SerializedExecutorService {

    /** 状态 **/
    private volatile boolean running;
    /** 线程工厂 **/
    private final TaskWorkerFactory taskWorkerFactory;
    /** ID容器 **/
    private final ConcurrentHashMap<Long, TaskWorker> id2TaskWorkerHolder;

    public GenericSerializedExecutorService(int threadNum, String threadNamePrefix) {
        this.running = true;
        this.taskWorkerFactory = new TaskWorkerFactory(threadNum, threadNamePrefix);
        this.id2TaskWorkerHolder = new ConcurrentHashMap<>();
    }

    @Override
    public Future<?> submit(Long id, Runnable task) {
        checkStatus();
        TaskWorker taskWorker = id2TaskWorkerHolder.computeIfAbsent(id, k -> taskWorkerFactory.allocation());
        return taskWorker.submitTask(task);
    }

    @Override
    public Future<?> submit(Runnable task) {
        checkStatus();
        TaskWorker taskWorker = taskWorkerFactory.allocation();
        taskWorker.finishOne();
        return taskWorker.submitTask(task);
    }

    @Override
    public void destroy(long id) {
        TaskWorker taskWorker = id2TaskWorkerHolder.remove(id);
        if (taskWorker != null) {
            taskWorker.finishOne();
        }
    }

    @Override
    public void shutdown() {
        this.running = false;
        this.taskWorkerFactory.shutdown();
        this.id2TaskWorkerHolder.clear();
    }

    private void checkStatus() throws RuntimeException {
        if (!running) {
            throw new RuntimeException("GenericSerializedExecutorService状态不为运行状态,无法执行任务");
        }
    }

    private static final class TaskWorker {

        /** 负担量 **/
        private final AtomicLong capacity = new AtomicLong(0);
        /** 分配的单线程池 **/
        private final ExecutorService executorService;

        public TaskWorker(ExecutorService executorService) {
            this.executorService = executorService;
        }

        Future<?> submitTask(Runnable task) {
            return executorService.submit(task);
        }

        void increment() {
            this.capacity.incrementAndGet();
        }

        void shutdown() {
            executorService.shutdown();
        }

        void finishOne() {
            this.capacity.decrementAndGet();
        }

        public long getCapacity() {
            return capacity.get();
        }
    }

    private static final class TaskWorkerFactory {
        /** 总线程池 **/
        private final TaskWorker[] taskWorkers;

        TaskWorkerFactory(int threadNum, String threadNamePrefix) {
            this.taskWorkers = new TaskWorker[threadNum];
            for (int i = 0; i < threadNum; i++) {
                ThreadPoolExecutor executor = new ThreadPoolExecutor(1, 1,
                        0, TimeUnit.SECONDS
                        , new ArrayBlockingQueue<>(5000)
                        , new NamedThreadFactory(threadNamePrefix + "-" + i, false));
                this.taskWorkers[i] = new TaskWorker(executor);
            }
        }

        TaskWorker allocation() {
            TaskWorker target = taskWorkers[0];
            for (int i = 1; i < taskWorkers.length; i++) {
                if (target.getCapacity() > taskWorkers[i].getCapacity()) {
                    target = taskWorkers[i];
                }
            }
            target.increment();
            return target;
        }

        void shutdown() {
            for (TaskWorker taskWorker : taskWorkers) {
                taskWorker.shutdown();
            }
        }

    }

}

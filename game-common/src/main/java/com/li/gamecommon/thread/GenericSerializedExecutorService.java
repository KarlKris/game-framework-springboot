package com.li.gamecommon.thread;

import java.util.Queue;

/**
 * 业务线程池实现类
 * @author li-yuanwen
 * @date 2022/2/8
 */
public class GenericSerializedExecutorService  {





    /** 任务源对象 **/
    private final class IdTaskContext {

        /** 状态 **/
        volatile boolean execute;
        /** 待执行任务队列 **/
        Queue<Runnable> waitToExecTaskQueue;
        /** 新任务队列 **/
        Queue<Runnable> newTaskQueue;

    }


    /** 工人 **/
    private final class TaskWorker implements Runnable {

        /** 工作线程 **/
        Thread thread;


        @Override
        public void run() {

        }
    }


}

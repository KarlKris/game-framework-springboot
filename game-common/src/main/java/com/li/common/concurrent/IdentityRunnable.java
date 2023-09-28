package com.li.common.concurrent;

public interface IdentityRunnable extends Runnable {

    /** 任务标识 **/
    Object getIdentity();

}

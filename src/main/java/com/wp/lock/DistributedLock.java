package com.wp.lock;

import java.util.concurrent.TimeUnit;

/**
 * @author 王萍
 * @date 2018/2/2 0002
 */
public interface DistributedLock {

    void lock();

    boolean tryLock();

    boolean tryLock(long time, TimeUnit unit);

    void unlock();
}

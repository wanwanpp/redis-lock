package com.wp.test;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class LockTest {
    public static void main(String[] args) {
        ThreadPoolExecutor pool = new ThreadPoolExecutor(5, 100,
                60, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(10),
                new ThreadFactory() {
                    AtomicInteger count = new AtomicInteger(1);
                    @Override
                    public Thread newThread(Runnable r) {
                        return new Thread(r, "thread-"+count.getAndIncrement());
                    }
                });
        for (int i = 0; i < 25; i++) {
            pool.execute(new Task("wanwanpp"));
            pool.execute(new Task("玩玩跑跑"));
            pool.execute(new Task("123456"));
            pool.execute(new Task("快过年啦！"));
        }
    }
}
package com.wp.test;

import com.wp.lock.RedisLock;

public class LockTest {
    //不加锁
    static class Outputer {
        /**
         * 创建一个名为redisLock的RedisLock类型的锁
         */
        RedisLock redisLock = new RedisLock("redisLock");

        public void output(String name) {
            redisLock.lock();
            try {
                for (int i = 0; i < name.length(); i++) {
                    System.out.print(name.charAt(i));
                    Thread.sleep(1000);
                }
                System.out.println();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                redisLock.unlock();
            }
        }
    }

    public static void main(String[] args) {
        final Outputer output = new Outputer();
        //线程1打印zhangsan
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    output.output("zhangsan");
                }
            }
        }, "thread1").start();

        //线程2打印lingsi
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    output.output("lingsi");
                }
            }
        }, "thread2").start();

        //线程3打印huangwu
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    output.output("huangwu");
                }
            }
        }, "thread3").start();
    }
}
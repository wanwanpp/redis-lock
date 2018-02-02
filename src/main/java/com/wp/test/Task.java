package com.wp.test;

import com.wp.lock.RedisLock;

public class Task implements Runnable {

    private String printThings;
    private static final RedisLock REDIS_LOCK = new RedisLock("REDIS_LOCK");

    public Task(String printThings) {
        this.printThings = printThings;
    }

    @Override
    public void run() {

        while (true) {
            try {
                Thread.sleep(3000);
                REDIS_LOCK.lock();
                for (int i = 0; i < printThings.length(); i++) {
                    System.out.print(printThings.charAt(i));
                    Thread.sleep(300);
                }
                System.out.println();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                REDIS_LOCK.unlock();
            }
        }
    }
}
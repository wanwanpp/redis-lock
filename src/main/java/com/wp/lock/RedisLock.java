package com.wp.lock;

import com.wp.utils.RedisUtil;
import redis.clients.jedis.Jedis;

import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class RedisLock implements Lock {

    /**
     * 存储到redis中的锁标志
     */
    private static final String LOCKED = "LOCKED";
    /**
     * 锁的有效时间(s)
     */
    public static final int EXPIRE = 60;
    private String key;
    private volatile boolean isLocked = false;
    private volatile Thread lockOwner = null;

    private ThreadLocal<Jedis> localJedis = new ThreadLocal<Jedis>() {
        @Override
        protected Jedis initialValue() {
            return RedisUtil.getJedis();
        }
    };

    public RedisLock(String key) {
        this.key = key;
    }

    @Override
    public void lock() {
        final Random r = new Random();
        try {
            while (true) {
                //将锁作为key存储到redis缓存中，存储成功则获得锁
                if (1 == localJedis.get().setnx(key.getBytes(), LOCKED.getBytes())) {
                    //设置锁的有效期，也是锁的自动释放时间，也是一个客户端在其他客户端能抢占锁之前可以执行任务的时间
                    //可以防止因异常情况无法释放锁而造成死锁情况的发生
                    localJedis.get().expire(key, EXPIRE);
                    //写入成功才会设为true。
                    isLocked = true;
                    lockOwner = Thread.currentThread();
                    break;
                }
                //获取锁失败时，应该在随机延时后进行重试，避免不同客户端同时重试导致谁都无法拿到锁的情况出现
                Thread.sleep(r.nextInt(5) + 5);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void unlock() {
        //释放锁的条件，锁处于锁定状态且当前线程是拥有所得线程。
        if (isLocked && Thread.currentThread().equals(lockOwner)) {
            localJedis.get().del(key);
        }
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {
    }

    @Override
    public boolean tryLock() {
        if (1 == localJedis.get().setnx(key.getBytes(), LOCKED.getBytes())) {
            localJedis.get().expire(key, EXPIRE);
            isLocked = true;
            lockOwner = Thread.currentThread();
            return true;
        }
        return false;
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        long nanos = unit.toNanos(time);
        long nowTime = System.nanoTime();
        final Random r = new Random();
        try {
            while ((System.nanoTime() - nowTime) < nanos) {
                if (1 == localJedis.get().setnx(key.getBytes(), LOCKED.getBytes())) {
                    localJedis.get().expire(key, EXPIRE);
                    isLocked = true;
                    lockOwner = Thread.currentThread();
                    return true;
                }
                Thread.sleep(r.nextInt(5) + 5);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public Condition newCondition() {
        return null;
    }
}
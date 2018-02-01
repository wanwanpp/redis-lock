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
     * 请求锁的超时时间(ms)
     */
    private static final long TIME_OUT = 30000;

    /**
     * 锁的有效时间(s)
     */
    public static final int EXPIRE = 60;
    private String key;
    private volatile boolean isLocked = false;

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
        //系统当前时间，纳秒
        long nowTime = System.nanoTime();
        //请求锁超时时间，毫秒 .           毫秒与纳秒相差1000000
        long timeout = TIME_OUT * 1000000;
        final Random r = new Random();
        try {
            //不断循环向Master节点请求锁，当请求时间(System.nanoTime() - nanoTime)超过设定的超时时间则放弃请求锁
            //这个可以防止一个客户端在某个宕掉的master节点上阻塞过长时间
            //如果一个master节点不可用了，应该尽快尝试下一个master节点
            while ((System.nanoTime() - nowTime) < timeout) {
                //将锁作为key存储到redis缓存中，存储成功则获得锁
                if (1==localJedis.get().setnx(key.getBytes(), LOCKED.getBytes())) {
                    //设置锁的有效期，也是锁的自动释放时间，也是一个客户端在其他客户端能抢占锁之前可以执行任务的时间
                    //可以防止因异常情况无法释放锁而造成死锁情况的发生
                    localJedis.get().expire(key, EXPIRE);
                    //写入成功才会设为true。
                    isLocked = true;
                    //上锁成功结束请求
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
        //释放锁
        //不管请求锁是否成功，只要已经上锁，客户端都会进行释放锁的操作
        if (isLocked) {
            localJedis.get().del(key);
        }
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean tryLock() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public Condition newCondition() {
        // TODO Auto-generated method stub
        return null;
    }
}
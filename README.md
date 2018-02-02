# redis-lock
### 一款基于redis实现的分布式锁
#### 提供以下接口的实现：
```
public interface DistributedLock {

    void lock();

    boolean tryLock();

    boolean tryLock(long time, TimeUnit unit);

    void unlock();
}
```
### 扩展点
- 目前是在redis单机上锁，可扩展为多机以保证可用性。
- 可实现可重入锁，公平锁，读写锁。

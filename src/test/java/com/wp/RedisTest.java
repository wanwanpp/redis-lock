package com.wp;

import com.wp.dao.IUserDao;
import com.wp.entity.User;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:applicationContext.xml"})
public class RedisTest {

    @Autowired
    private IUserDao userDao;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Test
    public void testSaveUser() {
        User user = new User();
        user.setId("402891815170e8de015170f6520b0000");
        user.setUserName("zhangsan");
        boolean res = userDao.save(user);
        Assert.assertTrue(res);
    }

    @Test
    public void testGetUser() {
        User user = new User();
        user = userDao.find("402891815170e8de015170f6520b0000");
        System.out.println(user.getId() + "-" + user.getUserName());
    }

    @Test
    public void testUpdateUser() {
        User user = new User();
        user.setId("402891815170e8de015170f6520b0000");
        user.setUserName("lisi");
        boolean res = userDao.update(user);
        Assert.assertTrue(res);
    }

    @Test
    public void testupdateUser() {
        User user = new User();
        user.setId("402891815170e8de015170f6520b0000");
        user.setUserName("wangping");
        redisTemplate.opsForValue().set(user.getId(), user.getUserName());
        //BoundValueOPerations需要先对key进行绑定。方法签名为public BoundValueOperations<K, V> boundValueOps(K key)
        //将key作为参数传入，返回BoundValueOperations对象。
        BoundValueOperations<String, Object> boundValueOps = redisTemplate.boundValueOps(user.getId());
    }

    @Test
    public void testDeleteUser() {
        boolean res = userDao.delete("402891815170e8de015170f6520b0000");
        Assert.assertTrue(res);
    }

    @Test
    public void testHash() {
        final RedisSerializer keySerializer = redisTemplate.getHashKeySerializer();
        final RedisSerializer valueSerializer = redisTemplate.getHashValueSerializer();
        final RedisSerializer stringSerializer = redisTemplate.getStringSerializer();
//        redisTemplate.getser
        final User user = new User();
        user.setId("123456");
        user.setUserName("王萍");
        redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection redisConnection) throws DataAccessException {
                //被序列化的对象需要implements Serializable
                return redisConnection.hSet(stringSerializer.serialize("user"), "wangping".getBytes(), valueSerializer.serialize(user));
            }
        });
    }

}
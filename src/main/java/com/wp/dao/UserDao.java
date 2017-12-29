package com.wp.dao;

import com.wp.entity.User;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Repository;

@Repository
public class UserDao extends BaseRedisDao<String, User> implements IUserDao {

    @Override
    public boolean save(final User user) {
        boolean res = redisTemplate.execute(new RedisCallback<Boolean>() {
            @Override
            public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
                RedisSerializer<String> serializer = redisTemplate.getStringSerializer();
                byte[] key = serializer.serialize(user.getId());
                byte[] value = serializer.serialize(user.getUserName());
                //set not exits
                return connection.setNX(key, value);
            }
        });
        return res;
    }

    @Override
    public boolean update(final User user) {
        boolean result = redisTemplate.execute(new RedisCallback<Boolean>() {
            public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
                RedisSerializer<String> serializer = redisTemplate.getStringSerializer();
                byte[] key = serializer.serialize(user.getId());
                byte[] name = serializer.serialize(user.getUserName());
                //set
                connection.set(key, name);
                return true;
            }
        });
        return result;
    }

    @Override
    public User find(final String userId) {
        User result = redisTemplate.execute(new RedisCallback<User>() {
            public User doInRedis(RedisConnection connection) throws DataAccessException {
                RedisSerializer<String> serializer = redisTemplate.getStringSerializer();
                byte[] key = serializer.serialize(userId);
                //get
                byte[] value = connection.get(key);
                if (value == null) {
                    return null;
                }
                String name = serializer.deserialize(value);
                User resUser = new User();
                resUser.setId(userId);
                resUser.setUserName(name);
                return resUser;
            }
        });
        return result;
    }

    @Override
    public boolean delete(final String userId) {
        boolean result = redisTemplate.execute(new RedisCallback<Boolean>() {
            public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
                RedisSerializer<String> serializer = redisTemplate.getStringSerializer();
                byte[] key = serializer.serialize(userId);
                //delete
                connection.del(key);
                return true;
            }
        });
        return result;
    }

}
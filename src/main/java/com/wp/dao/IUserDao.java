package com.wp.dao;

import com.wp.entity.User;

public interface IUserDao {
     
    public boolean save(User user);
     
    public boolean update(User user);
 
    public boolean delete(String userIds);
     
    public User find(String userId);
     
}
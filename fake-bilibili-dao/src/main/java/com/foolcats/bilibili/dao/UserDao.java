package com.foolcats.bilibili.dao;

import com.foolcats.bilibili.domain.User;
import com.foolcats.bilibili.domain.UserInfo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserDao {
    Integer addUser(User user);


    User getUserByPhone(String phone);

    Integer addUserInfo(UserInfo userInfo);
}

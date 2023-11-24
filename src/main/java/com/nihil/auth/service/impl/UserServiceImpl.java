package com.nihil.auth.service.impl;


import com.nihil.auth.mapper.UserMapper;
import com.nihil.auth.service.UserService;
import com.nihil.common.auth.AuthUser;
import com.nihil.common.response.Result;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Resource
    UserMapper userMapper;

    @Override
    public Boolean addUser(AuthUser user) {
        return userMapper.addUser(user) == 1;
    }

    @Override
    public AuthUser getUserByUid(Long uid) {
        return userMapper.getUserByUid(uid);
    }

    @Override
    public AuthUser changeUser(AuthUser user) {
        if(userMapper.changeUser(user) == 1) {
            return user;
        }
        return null;
    }

    @Override
    public Boolean deleteUserByUid(Long uid) {
        return  userMapper.deleteUserByUid(uid) == 1;
    }

    @Override
    public List<AuthUser> getList(Integer page, Integer size) {
        return userMapper.getList((page-1)*size, size);
    }
}

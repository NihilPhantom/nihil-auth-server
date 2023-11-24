package com.nihil.auth.mapper;


import com.nihil.common.auth.AuthUser;
import com.nihil.common.response.Result;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public
interface UserMapper {
    AuthUser getUserByUsernameAndClientId(String username, Long clientId);

    AuthUser getUserByUsername(String username);

    Integer addUser(AuthUser user);

    AuthUser getUserByUid(Long uid);

    Integer changeUser(AuthUser user);

    Integer deleteUserByUid(Long uid);

    List<AuthUser> getList(Integer offset, Integer size);
}

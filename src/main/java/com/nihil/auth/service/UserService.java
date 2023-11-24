package com.nihil.auth.service;

import com.nihil.common.auth.AuthUser;
import com.nihil.common.response.Result;

import java.util.List;

public interface UserService {
    
    /* 添加新用户 */
    Boolean addUser(AuthUser user);

    /* 查询用户信息 */
    AuthUser getUserByUid(Long uid);

    /* 修改用户信息 */
    AuthUser changeUser(AuthUser user);

    /* 删除用户 */
    Boolean deleteUserByUid(Long uid);

    /* 查询用户列表 */
    List<AuthUser> getList(Integer page, Integer size);
}

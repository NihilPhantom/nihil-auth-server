package com.nihil.auth.service.impl;

import com.nihil.auth.AuthorityService;
import com.nihil.auth.service.TokenService;
import com.nihil.auth.entity.AuthClient;
import com.nihil.auth.mapper.AuthRoleMapper;
import com.nihil.auth.pojo.Session;
import com.nihil.auth.service.CacheService;
import com.nihil.common.auth.AuthUser;
import com.nihil.common.utils.RandomUtils;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TokenServiceImp implements TokenService {

    @Autowired
    private AuthorityService authorityService;

    @Resource
    AuthRoleMapper roleMapper;


    private static final String ClientTokenKey = "client:";

    @Autowired
    CacheService cacheService;

    @Override
    public String user2Token(AuthUser user) {
        // 向 Authority 中添加用户ID
        Session session = new Session();
        session.setId(user.getId().toString());

        // 向 Authority 中添加用户角色、权限列表
        List<Long> userAuthoritys = authorityService.getAuthority(user);
        session.setAuthorities(userAuthoritys);
        List<Integer> userRoles = roleMapper.getRoleIdListByUid(user.getId());
        session.setRoles(userRoles);
        session.set("uid", user.getId());

        // 执行 定制化信息载入
        customSession(user, session);

        // 生成 随机token字符串，
        String token = RandomUtils.uuid();

        // 将 用户Session 中 缓存中
        cacheService.addSession(token, session);

        return token;
    }

    @Override
    public String client2Token(AuthClient client) {
        // 生成 随机token字符串，
//        String token =  RandomUtils.uuid();
//        redisExample.setValue(ClientTokenKey + token, client.getId());
//        return token;
        return "";
    }

    @Override
    public String token2ClientId(String session) {
        return null;
    }


    @Override
    public Session token2Session(String token) {
        return cacheService.getSession(token);
    }

    @Override
    public void customSession(AuthUser user, Session session) {

    }
}

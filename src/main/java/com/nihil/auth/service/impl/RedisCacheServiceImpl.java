package com.nihil.auth.service.impl;

import com.nihil.auth.entity.AuthAuthority;
import com.nihil.auth.entity.AuthResource;
import com.nihil.auth.entity.AuthRole;
import com.nihil.auth.pojo.Session;
import com.nihil.auth.pojo.requestparam.UserAuthority;
import com.nihil.auth.service.CacheService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@ConditionalOnProperty(
        name = "nihil-auth.cache-type",
        havingValue = "redis"
)
public class RedisCacheServiceImpl implements CacheService {

    @Override
    public void addSession(String key, Session o) {

    }

    @Override
    public Session getSession(String key) {
        return null;
    }

    @Override
    public void addResource(AuthResource authResource) {

    }

    @Override
    public Long getResIdByUrlAndMethod(String url, String method) {
        return null;
    }

    @Override
    public void addRoleResTie(Integer roleId, List<Long> resList) {

    }

    @Override
    public void delRoleResTie(Integer roleId, List<Long> resList) {

    }

    @Override
    public List<Integer> getRoleIdListByResId(Long resId) {
        return null;
    }

    @Override
    public Long getUidByToken(String token) {
        return null;
    }

    @Override
    public List<String> getAccessResByUid(Long uid) {
        return null;
    }

    @Override
    public List<AuthRole> getRolesByUid(Long uid) {
        return null;
    }

    @Override
    public List<AuthAuthority> getAuthoritiesByUid(Long uid) {
        return null;
    }

    @Override
    public List<Integer> getRolesByRequest(String requestPath, String requestMethod) {
        return null;
    }

    @Override
    public void delRes(AuthResource item) {

    }

    @Override
    public void setAuthorities(Long uid, UserAuthority userAuthority) {

    }
}

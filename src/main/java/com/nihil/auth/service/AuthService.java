package com.nihil.auth.service;

import com.nihil.auth.entity.AuthRoleRes;
import com.nihil.auth.pojo.AuthRoleUrl;
import com.nihil.auth.pojo.Session;
import com.nihil.auth.pojo.requestparam.UserAuthority;
import com.nihil.common.auth.AuthUser;

import java.util.List;

public interface AuthService {
    Session getSession(String token);

    String login(AuthUser user, Long tenantId);
    
    String login(AuthUser user);

    Integer changeRoleUrlMap(AuthRoleUrl roleUrlList);

    UserAuthority getUserAuthority(Long uid);

    List<AuthRoleRes> changeUserAuthority(Long uid, UserAuthority userAuthority);
}

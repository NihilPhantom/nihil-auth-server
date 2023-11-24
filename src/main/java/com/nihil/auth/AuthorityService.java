package com.nihil.auth;

import com.nihil.common.auth.AuthUser;

import java.util.List;

public interface AuthorityService {
    List<Long> getAuthority(AuthUser user);
}

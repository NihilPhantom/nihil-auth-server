package com.nihil.auth.service;

import com.nihil.auth.AuthorityService;
import com.nihil.auth.mapper.AuthMapper;
import com.nihil.common.auth.AuthUser;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
//@ConditionalOnProperty(name = "nihil-auth.data-isolation", havingValue = "none")
public class NoneAuthorityServiceImp implements AuthorityService {

    @Resource
    AuthMapper authMapper;

    @Override
    public List<Long> getAuthority(AuthUser user) {
//        List<Long> collect = authMapper.getRoleByUserId(user.getId()).stream().map(item -> item.getRoleId()).collect(Collectors.toList());
//        return collect;
        return null;
    }
}

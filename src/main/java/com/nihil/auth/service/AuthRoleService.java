package com.nihil.auth.service;


import com.nihil.auth.entity.AuthResource;
import com.nihil.auth.entity.AuthRole;
import com.nihil.auth.entity.AuthRoleRes;
import com.nihil.auth.pojo.RoleResListParam;

import java.util.List;

public interface AuthRoleService {

    Boolean addRole(AuthRole role);

    List<AuthRole> getRoleList();

    List<AuthRoleRes> getResListByRoleId(Integer roleId);

    boolean changeRole(AuthRole role);

    boolean changeRoleResList(RoleResListParam param);

    boolean deleteRoleResList(RoleResListParam param);

    boolean deleteRole(Integer roleId);

    /* 根据用户Id 获取用户所拥有的角色 */
    List<AuthRole> getRoleListByUid(Long uid);

    /* 检测角色列表中，是否包含拥有该请求权限的 */
    boolean checkRequestInIdList(String requestpath, String requestMethod, List<Integer> roles);
}

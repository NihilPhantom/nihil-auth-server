package com.nihil.auth.mapper;

import com.nihil.auth.entity.AuthResource;
import com.nihil.auth.entity.AuthRole;
import com.nihil.auth.entity.AuthRoleRes;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AuthRoleMapper {
    Integer addRole(AuthRole role);

    List<AuthRole> getRoleList();

    List<AuthRoleRes> getResListByRoleId(Integer roleId);

    Integer changeRole(AuthRole role);

    Integer deleteAllRoleByUserId(Long roleId);

    Integer addRoleResList(Integer roleId, List<Long> resIdList);

    Integer deleteRoleResList(Integer roleId, List<Long> resIdList);

    Integer deleteRole(Integer id);

    List<AuthRole> getRoleListByUid(Long uid);

    List<Integer> getRoleIdListByUid(Long uid);

    Integer addUserRoleList(Long uid, List<Integer> newRoleIdList);

    Integer delUserRoleList(Long uid, List<Integer> needDeleteRoleIdList);
}

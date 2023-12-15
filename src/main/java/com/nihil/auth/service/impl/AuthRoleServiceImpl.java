package com.nihil.auth.service.impl;


import com.nihil.auth.entity.AuthRole;
import com.nihil.auth.entity.AuthRoleRes;
import com.nihil.auth.mapper.AuthRoleMapper;
import com.nihil.auth.pojo.RoleResListParam;
import com.nihil.auth.service.AuthRoleService;
import com.nihil.auth.service.CacheService;
import com.nihil.auth.util.CollectUtil;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AuthRoleServiceImpl implements AuthRoleService {
    @Resource
    AuthRoleMapper authRoleMapper;

    @Resource
    CacheService cacheService;


    /* 加载完成后，将数据库中的 角色id和资源id的绑定 全部移入到缓存中 */
    @PostConstruct
    void init(){
        List<Integer> roleIdList = authRoleMapper.getRoleList().stream().map(AuthRole::getId).toList();
        for(Integer roleId: roleIdList){
            List<AuthRoleRes> resList = authRoleMapper.getResListByRoleId(roleId);
            if(resList != null){
                cacheService.addRoleResTie(roleId, resList.stream().map(AuthRoleRes::getResourceId).toList());
            }

        }
    }


    @Override
    public Boolean addRole(AuthRole role) {
        return authRoleMapper.addRole(role).equals(1);
    }

    @Override
    public List<AuthRole> getRoleList() {
        return authRoleMapper.getRoleList();
    }

    @Override
    public List<AuthRoleRes> getResListByRoleId(Integer roleId) {
        return authRoleMapper.getResListByRoleId(roleId);
    }

    @Override
    public boolean changeRole(AuthRole role) {
        return authRoleMapper.changeRole(role).equals(1);
    }

    @Override
    public boolean changeRoleResList(RoleResListParam param) {

        // 获取数据库中角色拥有的所有资源id
        List<Long> resIdListInDatabase = authRoleMapper.getResListByRoleId(param.getRoleId())
                .stream().map(AuthRoleRes::getResourceId).toList();

        List<Long> newResIdList = param.getResIdList();

        List<Long> delList = new ArrayList<>();

        // 删除重复的，resIdInDatabase 剩余的就是要删除的，newResIdList 中就是要添加的
        resIdListInDatabase.forEach(resId -> {
            if(!newResIdList.remove(resId)){
                delList.add(resId);
            }
        });

        // 删除 所有的 需要删除的 res
        Integer res = 0;
        if(delList.size() !=0){
            res = authRoleMapper.deleteRoleResList(param.getRoleId(), delList);
            cacheService.delRoleResTie(param.getRoleId(), delList);
        }

        // 添加 resList
        if(newResIdList.size() !=0){
            res += authRoleMapper.addRoleResList(param.getRoleId(), newResIdList);
            cacheService.addRoleResTie(param.getRoleId(), newResIdList);
        }

        return !res.equals(0);
    }

    @Override
    public boolean deleteRoleResList(RoleResListParam param) {
        List<Long> idList = param.getResIdList();
        return !authRoleMapper.deleteRoleResList(param.getRoleId(), idList).equals(0);
    }

    @Override
    public boolean deleteRole(Integer roleId) {
        // 查询该角色所控制的权限列表，将他们从缓存中删除
        List<AuthRoleRes> authRoleResList = authRoleMapper.getResListByRoleId(roleId);
        if(authRoleResList != null){
            List<Long> resList = authRoleMapper.getResListByRoleId(roleId).stream().map(AuthRoleRes::getResourceId).toList();
            cacheService.delRoleResTie(roleId, resList);
        }
        return authRoleMapper.deleteRole(roleId).equals(1);
    }

    @Override
    public List<AuthRole> getRoleListByUid(Long uid) {
        return authRoleMapper.getRoleListByUid(uid);
    }

    @Override
    public boolean checkRequestInIdList(String requestPath, String requestMethod, List<Integer> roles) {
        List<Integer> methodRoleList = cacheService.getRolesByRequest(requestPath, requestMethod);
        return CollectUtil.hasSame(roles, methodRoleList);
    }
}

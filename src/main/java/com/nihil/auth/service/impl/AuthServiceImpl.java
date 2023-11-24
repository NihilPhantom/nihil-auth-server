package com.nihil.auth.service.impl;

import com.nihil.auth.entity.AuthRoleRes;
import com.nihil.auth.service.CacheService;
import com.nihil.auth.service.TokenService;
import com.nihil.auth.mapper.AuthMapper;
import com.nihil.auth.mapper.AuthResourceMapper;
import com.nihil.auth.mapper.AuthRoleMapper;
import com.nihil.auth.mapper.UserMapper;
import com.nihil.auth.pojo.AuthRoleUrl;
import com.nihil.auth.pojo.Session;
import com.nihil.auth.pojo.requestparam.UserAuthority;
import com.nihil.auth.service.AuthService;
import com.nihil.common.auth.AuthUser;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Service
public class AuthServiceImpl implements AuthService {
    @Resource
    AuthMapper authMapper;

    @Resource
    UserMapper userMapper;

    @Resource
    TokenService tokenService;

    @Resource
    AuthRoleMapper authRoleMapper;

    @Resource
    AuthResourceMapper authResourceMapper;

    @Resource
    CacheService cacheService;

    /**
     * 用户登录方法：
     * 颁发token， token 将包含，{userId， [roleId]}
     * @param user 用户信息，{userName, password}
     * @return token
     */
    public String login(AuthUser user, Long cid) {
        // 用户认证
        AuthUser user2 = userMapper.getUserByUsernameAndClientId(user.getUsername(), cid);
        if(!user2.getPassword().equals(user.getPassword())){
            throw new RuntimeException("用户信息错误");
        }
        // 用户授权
        String token = tokenService.user2Token(user2);
        return token;
    }

    public String login(AuthUser user) {
        // 用户认证
        AuthUser user2 = userMapper.getUserByUsername(user.getUsername());
        if(!user2.getPassword().equals(user.getPassword())){
            throw new RuntimeException("用户信息错误");
        }
        // 用户授权
        String token = tokenService.user2Token(user2);
        return token;
    }


    public Session getSession(String token) {
        return tokenService.token2Session(token);
    }

    /**
     * 获取所有 角色->资源url 的映射
     * @return 多对多的 {角色, 资源url}
     */
    public List<AuthRoleUrl> getAllRoleUrl(){
        return authMapper.getAllRoleUrl();
    }

    public Integer changeRoleUrlMap(AuthRoleUrl roleUrlList) {
//        roleUrlList.getRoleId();
//        roleUrlList.getUrl();
        return 1;
    }

    /**
     * 获取用户所能访问到的所有资源：
     *   1. 用户直接对应的资源
     *   2. 用户通过角色 间接 包含的资源
     * @param uid 用户 ID
     */
    public UserAuthority getUserAuthority(Long uid) {
        List<Integer> roleList = authRoleMapper.getRoleIdListByUid(uid);
        List<Long> resList = authResourceMapper.getListByUid(uid);
        UserAuthority userAuthority = new UserAuthority();
        userAuthority.setResList(resList);
        userAuthority.setRoleList(roleList);
        return userAuthority;
    }

    /**
     * 修改用户权限
     * 此方法会比较用户提交的罪行 角色 和 资源 与数据库中已有的 角色和权限列表做比较，
     * 添加那些新增的部分， 删除掉原本数据库中已经不需要的部分
     * @param uid 用户Id
     * @param userAuthority 用户权限，会
     * @return 返回出现冲突的角色用户， 冲突的判断标准为 删除的资源Id仍然包含在某个角色中，
     */
    @Override
    public List<AuthRoleRes> changeUserAuthority(Long uid, UserAuthority userAuthority) {
        // 角色更新
        List<Long> oldResIdList = authResourceMapper.getListByUid(uid);
        List<Long> newResIdList = userAuthority.getResList();
        List<Long> needDeleteResIdList = new ArrayList<>();
        Integer res = 0;
        oldResIdList.forEach( redId -> {
            if(!newResIdList.remove(redId)){
                needDeleteResIdList.add(redId);
            }
        });
        if (newResIdList!=null && newResIdList.size()!=0){
            res += authResourceMapper.addUserResList(uid, newResIdList);
        }
        if(needDeleteResIdList.size() != 0){
            res += authResourceMapper.delUserResList(uid, needDeleteResIdList);
        }

        // 资源校验
        List<Integer> newRoleIdList = userAuthority.getRoleList();
        Set<Integer> roleResIdSet = new HashSet<>(newRoleIdList);
        List<AuthRoleRes> warningList = new ArrayList<>();

        needDeleteResIdList.forEach(resId -> {
            List<Integer> roleIdList = cacheService.getRoleIdListByResId(resId);
            if(roleIdList != null){
                roleIdList.forEach(roleId -> {
                    if(roleResIdSet.contains(roleId)){
                        AuthRoleRes authRoleRes = new AuthRoleRes();
                        authRoleRes.setRoleId(roleId);
                        authRoleRes.setResourceId(resId);
                        warningList.add(authRoleRes);
                    }
                });
            }
        });

        // 资源更新
        List<Integer> oldRoleIdList = authRoleMapper.getRoleIdListByUid(uid);
        List<Integer> needDeleteRoleIdList = new ArrayList<>();
        oldRoleIdList.forEach( roleId -> {
            if(!newRoleIdList.remove(roleId)){
                needDeleteRoleIdList.add(roleId);
            }
        });
        if(newRoleIdList!=null && newRoleIdList.size()!=0){
            res += authRoleMapper.addUserRoleList(uid, newRoleIdList);
        }
        if(needDeleteRoleIdList.size()!=0){
            res += authRoleMapper.delUserRoleList(uid, needDeleteRoleIdList);
        }

        // 更新缓存中的用户角色
        cacheService.setAuthorities(uid, userAuthority);

        return warningList;
    }
}

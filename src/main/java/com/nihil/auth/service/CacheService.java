package com.nihil.auth.service;



import com.nihil.auth.entity.AuthAuthority;
import com.nihil.auth.entity.AuthResource;
import com.nihil.auth.entity.AuthRole;
import com.nihil.auth.pojo.Session;
import com.nihil.auth.pojo.requestparam.UserAuthority;

import java.util.List;

/**
 * 实现了缓存功能，存储系统运行过程中所产生的重复使用的数据
 */
public interface CacheService {

    void addSession(String key, Session o);

    Session getSession(String key);


    /* 添加一条资源的缓存 */
    void addResource(AuthResource authResource);

    /* 通过资源的 url 和 请求类型，获取到资源ID */
    Long getResIdByUrlAndMethod(String url, String method);

    /* 缓存一条【角色】到【资源】的关联 */
    void addRoleResTie(Integer roleId, List<Long> resList);

    /* 删除一条【角色】到【资源】的关联 */
    void delRoleResTie(Integer roleId, List<Long> resList);


    /* 获取一个资源可以被哪些角色访问到 */
    List<Integer> getRoleIdListByResId(Long resId);

    Long getUidByToken(String token);

    List<String> getAccessResByUid(Long uid);

    List<AuthRole> getRolesByUid(Long uid);

    List<AuthAuthority> getAuthoritiesByUid(Long uid);

    List<Integer> getRolesByRequest(String requestPath, String requestMethod);

    void delRes(AuthResource item);

    /* 重新设置角色的权限 */
    void setAuthorities(Long uid, UserAuthority userAuthority);
}

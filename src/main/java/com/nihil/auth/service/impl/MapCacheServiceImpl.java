package com.nihil.auth.service.impl;


import com.nihil.auth.entity.AuthAuthority;
import com.nihil.auth.entity.AuthResource;
import com.nihil.auth.entity.AuthRole;
import com.nihil.auth.pojo.Session;
import com.nihil.auth.pojo.requestparam.UserAuthority;
import com.nihil.auth.service.CacheService;
import com.nihil.auth.util.THasMap;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 本类使用默认的 Java map 实现了缓存的注册方法，
 * 使用 Java map 可以在不开启额外应用时快速启动服务，
 * 适合小型或者测试应用
 */
@Service
@ConditionalOnProperty(
        name = "nihil-auth.cache-type",
        havingValue = "map",
        matchIfMissing = true
)
public class MapCacheServiceImpl implements CacheService {

    @Value("${nihil-auth.session.expiration}")
    /* session 存活时间 */
    private long sessionExpirationTime;

    @Value("${time-has.timeout-task-threshold}")
    /* 任务数阈值：当任务数超过这个值时，将会每隔 一段时间进行 超时数据清洗 */
    private int timeoutTaskThreshold;

    @Value("${time-has.check-period}")
    /* 检查间隔：进行超时对象清理任务的时间间隔，以 ms 为单位 */
    private int checkPeriod;

    /* 存放 Session 的hasMap */
    THasMap<String, Session> sessionMap;

    /* 存放 【用户ID】->【SessionIdList】 的映射 */
    THasMap<Long, List<String>> uid2SessionIdListMap;

    /* 存放 【资源请求】 到资源 Id 的映射 */
    ConcurrentHashMap<String, Long> request2ResIdMap = new ConcurrentHashMap<>();

    /* 存放 【资源请求】 到角色 Id 列表的映射 */
    ConcurrentHashMap<Long, List<Integer>> resId2RoleIdListMap = new ConcurrentHashMap<>();

    @PostConstruct
    void init(){
        sessionMap = new THasMap<>(timeoutTaskThreshold, checkPeriod);
        uid2SessionIdListMap = new THasMap<>(timeoutTaskThreshold, 2*checkPeriod);
    }

    @Override
    public void addSession(String key, Session s) {
        sessionMap.put(key, s, sessionExpirationTime);
        Long uid = (Long) s.get("uid");
        if(uid2SessionIdListMap.containsKey(uid)){
            uid2SessionIdListMap.get(uid).add(key);
            uid2SessionIdListMap.setExpire(uid, sessionExpirationTime);
        }
    }

    @Override
    public Session getSession(String key) {
        return sessionMap.get(key);
    }

    @Override
    public void addResource(AuthResource authResource) {
        // TODO url 和 Method 的数处理
        request2ResIdMap.put( authResource.getUrl() + "|" + authResource.getMethod(), authResource.getId());
    }

    @Override
    public void addRoleResTie(Integer roleId, List<Long> resList) {
        for (int i=0; i<resList.size(); i++){
            if(!resId2RoleIdListMap.containsKey(resList.get(i))){
                resId2RoleIdListMap.put(resList.get(i), new ArrayList<>());
            }
            resId2RoleIdListMap.get(resList.get(i)).add(roleId);
        }
    }

    @Override
    public void delRoleResTie(Integer roleId, List<Long> resList) {
        for (int i=0; i<resList.size(); i++){
            if(resId2RoleIdListMap.containsKey(resList.get(i))){
                resId2RoleIdListMap.get(resList.get(i)).remove(roleId);
            }
        }
    }

    @Override
    public List<Integer> getRoleIdListByResId(Long resId) {
        return this.resId2RoleIdListMap.get(resId);
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
        // TODO requestPath 和 Method 的预处理

        // 通过 资源路径 和 请求方式 查询 资源Id
        Long resId = getResIdByUrlAndMethod(requestPath, requestMethod);
        List<Integer> res = resId2RoleIdListMap.get(resId);
        Long resId2 = getResIdByUrlAndMethod(requestPath, "ALL");
        res.addAll(resId2RoleIdListMap.get(resId2));
        return res;
    }

    @Override
    public void delRes(AuthResource resource) {
        this.request2ResIdMap.remove(resource.getUrl() + "|" + resource.getMethod());
        this.resId2RoleIdListMap.remove(resource.getId());
    }

    @Override
    public void setAuthorities(Long uid, UserAuthority userAuthority) {
        List<String> sessionList = uid2SessionIdListMap.get(uid);
        if(sessionList!=null){
            for(int i=sessionList.size()-1; i>=0; i--){
                Session session = sessionMap.get(sessionList.get(i));
                if(session==null){
                    sessionList.remove(i);
                }else{
                    session.setAuthorities(userAuthority.getResList());
                    session.setRoles(userAuthority.getRoleList());
                }
            }
        }
    }

    @Override
    public Long getResIdByUrlAndMethod(String url, String method) {
        // TODO requestPath 和 Method 的预处理
        return request2ResIdMap.get(url + "|" + method);
    }

}

package com.nihil.auth.service;

import com.nihil.auth.entity.AuthResource;
import com.nihil.auth.pojo.AddUserResourceParam;

import java.util.List;
import java.util.Map;

public interface ResourceService {

    /* 获取 Springboot 容器中已经注册的资源 */
    Map<String, List<AuthResource>> getResourceOnline();

    /* 从数据库中获取 资源列表 */
    List<AuthResource> getResourceInDataBase();

    /* 添加一条资源 */
    Boolean addResource(AuthResource res);

    /* 添加一条 用户和资源的映射 */
    Boolean addUserResource(AddUserResourceParam param);

    boolean deleteOverdueResource();

    /* 将一个资源加载到白名单中 ，在白名单中的资源不会在系统接口发生改变时，被系统自动删除 */
    Boolean addResToWhiteList(Long resId);

    Boolean removeResFromWhiteList(Long resId);

    List<AuthResource> getDirectResourceByUid(Integer uid);

    boolean checkRequestInIdList(String requestpath, String requestMethod, List<Long> authorities);

    boolean synchronizeRes();
}

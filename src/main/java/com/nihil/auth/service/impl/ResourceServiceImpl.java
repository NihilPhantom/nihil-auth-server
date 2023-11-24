package com.nihil.auth.service.impl;

import com.nihil.auth.entity.AuthResource;
import com.nihil.auth.mapper.AuthResourceMapper;
import com.nihil.auth.pojo.AddUserResourceParam;
import com.nihil.auth.service.CacheService;
import com.nihil.auth.service.ResourceService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.condition.RequestMethodsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ResourceServiceImpl implements ResourceService {

    /* 自动注入：注入 RequestMappingHandlerMapping，以获取所有的请求及路由  */
    @Resource
    private RequestMappingHandlerMapping requestMappingHandlerMapping;

    @Resource
    AuthResourceMapper resourceMapper;

    @Resource
    CacheService cacheService;


    /* 加载完成后，将数据库中的 Resource 全部移入到缓存中 */
    @PostConstruct
    void init(){
        List<AuthResource> resourceList = resourceMapper.getResource();
        for (AuthResource resource: resourceList){
            cacheService.addResource(resource);
        }
    }

    @Override
    public Map<String, List<AuthResource>> getResourceOnline() {
        Map<String, List<AuthResource>> mappings = new HashMap<>();

        for (Map.Entry<RequestMappingInfo, HandlerMethod> entry : requestMappingHandlerMapping.getHandlerMethods().entrySet()) {
            RequestMappingInfo info = entry.getKey();

            String controller = entry.getValue().toString().split("#")[0];

            if (mappings.containsKey(controller)) {
                mappings.get(controller).add(requestMappingInfo2AurhRes(info));
            } else {
                List<AuthResource> methods = new ArrayList<>();
                methods.add(requestMappingInfo2AurhRes(info));
                mappings.put(controller, methods);
            }
        }
        return mappings;
    }

    @Override
    public List<AuthResource> getResourceInDataBase() {
        return resourceMapper.getResource();
    }

    @Override
    public Boolean addResource(AuthResource res) {
        return resourceMapper.addResource(res).equals(1);
    }

    @Override
    public Boolean addUserResource(AddUserResourceParam param) {
        return resourceMapper.addUserResource(param).equals(1);
    }

    @Override
    public boolean deleteOverdueResource() {
        List<AuthResource> resInDataBase = resourceMapper.getResource();
        Map<String, List<AuthResource>> outdatedMap= new HashMap<>();

        return false;
    }

    @Override
    public Boolean addResToWhiteList(Long resId) {
        return resourceMapper.addResToWhiteList(resId).equals(1);
    }

    @Override
    public Boolean removeResFromWhiteList(Long resId) {
        return resourceMapper.removeResFromWhiteList(resId).equals(1);
    }

    @Override
    public List<AuthResource> getDirectResourceByUid(Integer uid) {
        return resourceMapper.getDirectResourceByUid(uid);
    }

    @Override
    public boolean checkRequestInIdList(String requestPath, String requestMethod, List<Long> authorities) {
        // 从缓存服务中查询根据请求方法和求情类型查找出资源id，判断其是否在authorities中
        Long resId1 = cacheService.getResIdByUrlAndMethod(requestPath, requestMethod);
        Long resId2 = cacheService.getResIdByUrlAndMethod(requestMethod, "ALL");
        return authorities.contains(resId1)||authorities.contains(resId2);
    }

    @Override
    public boolean synchronizeRes() {
        // 获取数据库资源
        List<AuthResource> resInDBList = resourceMapper.getResource();

        // 获取所有 Controller 的资源
        HashMap<String,Boolean> controllerResMap = new HashMap(resInDBList.size());
        for (Map.Entry<RequestMappingInfo, HandlerMethod> entry : requestMappingHandlerMapping.getHandlerMethods().entrySet()) {
            RequestMappingInfo info = entry.getKey();
            String controller = entry.getValue().toString().split("#")[0];
            AuthResource resource = requestMappingInfo2AurhRes(info);
            controllerResMap.put(resource.getUrl()+"|"+resource.getMethod(), true);
        }
        List<AuthResource> redundantResList = new ArrayList<>();  // 存放数据库中 过时的资源
        for (AuthResource resource : resInDBList){
            if(controllerResMap.containsKey(resource.getUrl()+"|"+resource.getMethod())){
                controllerResMap.remove(resource.getUrl()+"|"+resource.getMethod());
            }else{
                if(resource.getPermanent()==null || !resource.getPermanent()){
                    redundantResList.add(resource);
                }
            }
        }

        // 删除所有过期的资源
        if(redundantResList.size()!=0){
            resourceMapper.delResList(redundantResList);
            redundantResList.forEach(item-> cacheService.delRes(item));
        }

        // 向数据库插入新的资源
        List<AuthResource> addResList = controllerResMap.keySet().stream().map(item->{
            String[] split = item.split("\\|");
            AuthResource authResource = new AuthResource();
            authResource.setUrl(split[0]);
            authResource.setMethod(split[1]);
            return authResource;
        }).toList();

        resourceMapper.addResList(addResList);
        for (AuthResource resource : addResList){
            cacheService.addResource(resource);
        }

        return false;
    }

    /* 将RequestMappingInfo 转化为AuthResource 类 */
    public  AuthResource requestMappingInfo2AurhRes(RequestMappingInfo requestMappingInfo) {
        RequestMethodsRequestCondition methods = requestMappingInfo.getMethodsCondition();
        String method = methods.getMethods().isEmpty() ? "ALL" : methods.getMethods().iterator().next().name();
        String url = requestMappingInfo.getPathPatternsCondition().getPatterns().isEmpty() ? "" : requestMappingInfo.getPathPatternsCondition().getPatterns().iterator().next().getPatternString();

        AuthResource res = new AuthResource();
        res.setMethod(method);
        res.setUrl(url);
        return res;
    }
}

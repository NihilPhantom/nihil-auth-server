package com.nihil.auth.controller;

import com.nihil.auth.entity.AuthResource;
import com.nihil.auth.service.CacheService;
import com.nihil.auth.service.ResourceService;
import com.nihil.common.response.Result;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("cache")
public class CacheController {
    @Resource
    CacheService cacheService;

    @Resource
    ResourceService resourceService;

    @GetMapping("getRoleResCache")
    Result<Map<Long, List<Integer>>> getRoleResCache(){
        Map<Long, List<Integer>> res = new HashMap<>();
        List<AuthResource> resourceInDataBase = resourceService.getResourceInDataBase();
        resourceInDataBase.forEach( resource ->{
            res.put(resource.getId(), cacheService.getRoleIdListByResId(resource.getId()));
        });
        return Result.success(res);
    }
}

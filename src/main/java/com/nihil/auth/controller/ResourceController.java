package com.nihil.auth.controller;

import com.nihil.auth.entity.AuthResource;
import com.nihil.auth.pojo.AddUserResourceParam;
import com.nihil.auth.service.ResourceService;
import com.nihil.common.response.Result;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/res")
public class ResourceController {

    @Resource
    ResourceService resourceService;

    /* 扫描服务器现有资源，并进行展示 */
    @GetMapping("resOnline")
    @ResponseBody
    public Result<Map<String, List<AuthResource>>> getResourceOnline(){
        Map<String,List<AuthResource>> mappings = resourceService.getResourceOnline();
        return Result.success(mappings);
    }

    /* 查询数据库中的所有资源，进行返回 */
    @GetMapping("resIndatabase")
    @ResponseBody
    public Result<List<AuthResource>> getResourceInDatabase(){
        List<AuthResource> resList = resourceService.getResourceInDataBase();
        return Result.success(resList);
    }

    /* 同步 Controller 中的资源 到 数据库中 */
    @PostMapping("synchronizeRes")
    @ResponseBody
    public Result<Boolean> synchronizeRes(){
        Boolean res = resourceService.synchronizeRes();
        return Result.success(res);
    }

    /* 添加自定义资源 */
    @PostMapping
    @ResponseBody
    public Result<Boolean> addResource(AuthResource res){
        if(resourceService.addResource(res)){
            return Result.success();
        }
        return Result.failed();
    }

    /* 删除所有过期资源 */
    @DeleteMapping("overdue")
    @ResponseBody
    public Result<Boolean> deleteOverdueResource(){
        if(resourceService.deleteOverdueResource()){
            return Result.success();
        }
        return Result.failed();
    }

    /* 给用户添加资源 */
    @PostMapping("user")
    @ResponseBody
    public Result<Boolean> addUserResource(@RequestBody AddUserResourceParam param){
        if(resourceService.addUserResource(param)){
            return Result.success();
        }
        return Result.failed();
    }

    /* 获取用户直接拥有的资源 */
    @GetMapping("user")
    @ResponseBody
    public Result<List<AuthResource>> getUserResource(@RequestParam Integer uid){
        List<AuthResource> res =  resourceService.getDirectResourceByUid(uid);
        return Result.success(res);
    }

    /* 将资源路径添加 到 资源白名单 */
    @PutMapping("addWhiteList")
    @ResponseBody
    public Result<Boolean> addResToWhiteList(@RequestParam("id") Long resId){
        if(resourceService.addResToWhiteList(resId)){
            return Result.success();
        }
        return Result.failed();
    }

    /* 将资源路径从 资源白名单中删除 */
    @PutMapping("removeWhiteList")
    @ResponseBody
    public Result<Boolean> removeResFromWhiteList(@RequestParam("id") Long resId){
        if(resourceService.removeResFromWhiteList(resId)){
            return Result.success();
        }
        return Result.failed();
    }
}

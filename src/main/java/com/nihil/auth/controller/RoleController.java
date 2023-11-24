package com.nihil.auth.controller;

import com.nihil.auth.entity.AuthRole;
import com.nihil.auth.entity.AuthRoleRes;
import com.nihil.auth.pojo.RoleResListParam;
import com.nihil.auth.service.AuthRoleService;
import com.nihil.common.response.Result;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("role")
public class RoleController {

    @Resource
    AuthRoleService authRoleService;


    /* 添加一个新的角色 */
    @PostMapping
    public Result<Boolean> addRole(@RequestBody AuthRole role){
        if(authRoleService.addRole(role)){
            return Result.success();
        }
        return Result.failed();
    }

    /* 查询一个角色role 所包含的所有 资源 */
    @GetMapping("resList")
    public Result<List<AuthRoleRes>> getResListByRoleId(@RequestParam Integer roleId){
         List<AuthRoleRes> resourceList = authRoleService.getResListByRoleId(roleId);
         return Result.success(resourceList);
    }

    /* 查询一个用户 所包含的所有 资源 */
    @GetMapping("user")
    public Result<List<AuthRole>> getRoleListByUid(@RequestParam Long uid){
        List<AuthRole> resourceList = authRoleService.getRoleListByUid(uid);
        return Result.success(resourceList);
    }


    /* 查询所有的角色 */
    @GetMapping("list")
    public Result<List<AuthRole>> getRoleList(){
        List<AuthRole> roleList = authRoleService.getRoleList();
        return Result.success(roleList);
    }

    /* 修改角色名 */
    @PutMapping
    public Result<AuthRole> changeRole(AuthRole role){
        if(authRoleService.changeRole(role)){
            return Result.success();
        }
        return Result.failed();
    }

    /* 修改角色所包含的资源列表*/
    @PutMapping("resList")
    public Result<Boolean> changeRoleResList(@RequestBody RoleResListParam param){
        if(authRoleService.changeRoleResList(param)){
            return Result.success();
        }
        return Result.failed();
    }

    /* 从角色的资源列表中删除一系列的资源 */
    @DeleteMapping("resList")
    public Result<Boolean> deleteRoleResList(@RequestBody RoleResListParam param){
        if(authRoleService.deleteRoleResList(param)){
            return Result.success();
        }
        return Result.failed();
    }

    /* 删除一个角色 */
    @DeleteMapping
    public Result<Boolean> deleteRole(@RequestParam Integer roleId){
        if(authRoleService.deleteRole(roleId)){
            return Result.success();
        }
        return Result.failed();
    }

}

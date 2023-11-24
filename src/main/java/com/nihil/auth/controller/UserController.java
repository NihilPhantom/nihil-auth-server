package com.nihil.auth.controller;

import com.nihil.auth.exception.NihilAuthException;
import com.nihil.auth.service.UserService;
import com.nihil.common.auth.AuthUser;
import com.nihil.common.response.Result;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("user")
public class UserController {

    @Resource
    UserService userService;

    /* 查询所有的用户信息 */
    @GetMapping("list")
    @ResponseBody
    public Result<List<AuthUser>> getUserList(
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "100") Integer size){
        return Result.success(userService.getList(page, size));
    }

    /* 用户注册，用户注册可以更具不同的系统进行注册 */
    @PostMapping
    @ResponseBody
    public Result addUser(@RequestBody AuthUser user){
        if(userService.addUser(user)){
            return Result.success();
        }
        return Result.failed();
    }

    /* 删除用户 */
    @DeleteMapping
    @ResponseBody
    public Result deleteUser(@RequestParam Long uid){
        if(userService.deleteUserByUid(uid)){
            return Result.success();
        }
        return Result.failed();
    }

    /* 修改用户信息 */
    @PutMapping
    @ResponseBody
    public Result<AuthUser> changeUser(@RequestBody AuthUser user){
        if(user.getId() == null){
            throw new NihilAuthException("缺少 uid 参数");
        }
        return Result.success(userService.changeUser(user));
    }

    // 用户登录，传入注册系统的Id，返回值中包含预设的跳转地址

    // 用户管理，只有该系统的管理员能进行用户管理

}

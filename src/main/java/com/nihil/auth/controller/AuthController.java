package com.nihil.auth.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.nihil.auth.entity.AuthRoleRes;
import com.nihil.auth.service.*;
import com.nihil.auth.config.interceptor.AuthNoNeed;
import com.nihil.auth.exception.NihilAuthException;
import com.nihil.auth.pojo.AuthRoleUrl;
import com.nihil.auth.pojo.Session;
import com.nihil.auth.pojo.requestparam.UserAuthority;
import com.nihil.common.auth.AuthUser;
import com.nihil.common.response.Result;
import jakarta.annotation.Resource;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/auth")
public class AuthController extends BaseController{


    @Value("${nihil-auth.tenant.header}")
    String tenantHeader;

    @Value("${nihil-auth.tenant.enable}")
    private Boolean isTenantEnable;

    @Resource
    AuthService authService;

    @Resource
    TokenService tokenService;

    @Resource
    ClientService clientService;

    @Resource
    ResourceService resourceService;

    @Resource
    AuthRoleService authRoleService;


    @AuthNoNeed
    @GetMapping("/authorize")
    public String redirectToStaticPage(
            HttpServletRequest request, HttpServletResponse response,
            @CookieValue(value = "token", required = false) String token,
            @RequestParam String clientId,
            @RequestParam(value = "redirect_url",required = false) String redirect) throws JsonProcessingException {

        // 获取用户token，如果用户token存在的话，就直接返回用户信息
        if (token!=null && tokenService.token2Session(token)!=null) {
            Cookie cookie = new Cookie("myCookie", "cookieValue");
            response.addCookie(cookie);
            if(redirect==null){
                redirect = clientService.getRedirectList(clientId).get(0);
            }
            return "redirect:https://127.0.0.1/wnote/tool";
        }

        // 设置Cookie
        Cookie cookie = new Cookie("token", clientId);
        response.addCookie(cookie);

        // 重定向到静态页面
        return "redirect:/login.html";
    }

    @AuthNoNeed
    @PostMapping("/login")
    @ResponseBody
    public Result<String> loginFromWeb(
            @RequestParam String username,
            @RequestParam String password,
            @RequestHeader(value = "${nihil-auth.tenant.header}", required = false) Long tenantId
        ) {
        // 用户登录
        AuthUser user = new AuthUser(username,password);
        String res;
        if (isTenantEnable){
            if(tenantId == null){
                throw new NihilAuthException("租户Id丢失");
            }
            res = authService.login(user, tenantId);
        } else {
            res = authService.login(user);
        }

        return Result.success(res);
    }


    @GetMapping("/code")
    public void code2token(@RequestParam String code){

    }

//    @PostMapping("/auth/login")
//    @ResponseBody
//    public String login(
//            @RequestBody AuthUser user
//    ) {
//        String res = authService.login(user, cid);
//        if (res.equals("")) {
//            return "";
//        }
//        return res;
//    }

    @GetMapping("/session")
    @ResponseBody
    public Result<Session> getAuthority(@RequestParam String token) throws JsonProcessingException {
        return Result.success(authService.getSession(token));
    }

    @PutMapping("/role_url_map")
    @ResponseBody
    public Result changeRoleUrlMap(@RequestBody AuthRoleUrl roleUrlList) {
        int res = authService.changeRoleUrlMap(roleUrlList);
        return null;
    }

    /* 获取一个用户，所包含的所有权限 */
    @GetMapping("role_and_res")
    @ResponseBody
    public Result<UserAuthority> getUserAuthority (@RequestParam Long uid){
        return Result.success(authService.getUserAuthority(uid));
    }
    /**
     * 修改一个用户所包含的所有权限
     * @param uid
     * @param userAuthority
     * @return 返回值：如果【删除的资源】仍然在【角色】中，就会被存到结果中
     */
    @PutMapping("role_and_res")
    @ResponseBody
    public Result<List<AuthRoleRes>> changeUserAuthority(
            @RequestParam Long uid,
            @RequestBody UserAuthority userAuthority){
//        Integer res = resourceService.changeUserResList(userAuthority.getResList());
//        res += authRoleService.changeUserRoleList(userAuthority.getRoleList());
//        return  ;
        List<AuthRoleRes> warningList = authService.changeUserAuthority(uid, userAuthority);
        return Result.success(warningList);
    }
}
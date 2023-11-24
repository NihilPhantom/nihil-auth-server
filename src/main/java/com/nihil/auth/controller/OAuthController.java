package com.nihil.auth.controller;

import com.nihil.auth.CodeService;
import com.nihil.auth.service.TokenService;
import com.nihil.auth.config.interceptor.AuthNoNeed;
import com.nihil.auth.exception.NihilAuthException;
import com.nihil.auth.service.AuthService;
import com.nihil.common.auth.AuthUser;
import com.nihil.common.utils.RandomUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/oauth")
public class OAuthController {

    @Value("${nihil-auth.tenant.header}")
    String tenantHeader;

    @Value("${nihil-auth.tenant.enable}")
    private Boolean isTenantEnable;

    @Resource
    AuthService authService;

    @Resource
    TokenService redisTokenService;

    @Resource
    CodeService inMemoryCodeService;


    @AuthNoNeed
    @PostMapping("/login")
    public void loginFromWeb(
            HttpServletResponse response,
            @CookieValue(value = "token", required = false) String token,
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


        // 如果失败返回
        if (res.equals("")) {
            // 重定向到静态页面
//            return "redirect:/login.html?err";
            response.setHeader("Location", "/login.html?err");
//        return "redirect:https://127.0.0.1/wnote/tool";
            response.setStatus(HttpServletResponse.SC_FOUND);
        }

        // 如果成功获取用户token
        String code = RandomUtils.uuid();
        inMemoryCodeService.setCodeToken(code, token);
        Cookie cookie = new Cookie("myCookie", res);
        response.addCookie(cookie);
        response.setHeader("Location", "https://127.0.0.1/wnote/tool?code=" + code);
//        return "redirect:https://127.0.0.1/wnote/tool";
        response.setStatus(HttpServletResponse.SC_FOUND);
    }
}

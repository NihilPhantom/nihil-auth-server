package com.nihil.auth.config.interceptor;

import com.nihil.auth.pojo.Session;
import com.nihil.auth.service.AuthRoleService;
import com.nihil.auth.service.CacheService;
import com.nihil.auth.service.ResourceService;
import com.nihil.auth.service.TokenService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.util.ServletRequestPathUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@Component("authInterceptor")
public class AuthInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(HandlerInterceptor.class);

    @Value("${nihil-auth.token.header}")
    String tokenHeader;


    @Resource
    TokenService tokenService;

//    @Resource
//    ResourceService resourceService;

    @Resource
    CacheService cacheService;

    @Resource
    AuthRoleService authRoleService;


    /**
     * 这个方法将从3个方面来进行判断是否可以正常访问
     * 1， 放行跨域请求。
     * 2. 如果自己拥有 AuthNoNeed标注 或者所属的 class拥有 AuthNoNeed 就直接放行
     * 3. 根据用户角色，对请求资源进行选择性放行
     * @param request  包含请求参数
     * @param response 响应结果
     * @param handler  处理方法
     * @return 是否放行
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        // 1. 放行跨域请求。
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        // 2. 如果自己拥有NoNeedToken标注或者所属的class拥有NoNeedToken 就直接放行
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        if(handlerMethod.getMethodAnnotation(AuthNoNeed.class)!=null
                || handlerMethod.getBeanType().isAnnotationPresent(AuthNoNeed.class)){
            return true;
        }

        // 3. 根据用户角色，对请求资源进行选择性放行
        String token = request.getHeader(tokenHeader);
        if (token != null) {
            String requestPath = ServletRequestPathUtils.getCachedPath(request).toString();  // 获取请求路径
            String requestMethod = request.getMethod();  // 获取请求的类型
            logger.info("【"+requestPath + "|" + requestMethod + "】");

            // 根据用户 token 获取用户所拥有的资源列表
            Session session = tokenService.token2Session(token);
            if(session == null){
                sayNo(response);
                return false;
            }
            List<Long> authorities = session.getAuthorities();
            List<Integer> roles = session.getRoles();

            // 放行超级管理员
            if(roles.contains(1)){
                return true;
            }

            // 从缓存服务中查询根据请求方法和求情类型查找出资源id，判断其是否在authorities中
            boolean isRequestInIdList = false;
            if(authorities != null){
                Long resId1 = cacheService.getResIdByUrlAndMethod(requestPath, requestMethod);
                Long resId2 = cacheService.getResIdByUrlAndMethod(requestMethod, "ALL");
                isRequestInIdList =  authorities.contains(resId1)||authorities.contains(resId2);
            }

            if(isRequestInIdList
                || authRoleService.checkRequestInIdList(requestPath, requestMethod, roles)){
                return true;
            }
        }

        // 多租户模式下的放行逻辑
//        String clientId = tokenService.token2ClientId(token);
//        if(clientId != null){
//            request.setAttribute("client_id", clientId);
//            return true;
//        }
        sayNo(response);
        return false;
    }

    /* 向输出中填入 403 */
    private void sayNo(HttpServletResponse response) throws IOException {
        response.setContentType("application/json");  // 设置响应的 Content-Type 为 application/json
        response.setCharacterEncoding("UTF-8");  // 设置响应的字符编码为 UTF-8
        response.setStatus(403);
        PrintWriter out = response.getWriter();
        out.append("{\"msg\":\"权限不足\"}");
    }
}

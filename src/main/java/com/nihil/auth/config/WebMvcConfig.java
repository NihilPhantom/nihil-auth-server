package com.nihil.auth.config;

import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;



@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private static final Logger logger = LoggerFactory.getLogger(WebMvcConfig.class);

    @Resource
    HandlerInterceptor authInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 初始化 AuthInterceptor 中 权限和访问路径的映射
//        for(AuthRoleUrl ru: authService.getAllRoleUrl()){
//            logger.info("== add new rule:" + ru.getRoleId() +"\t->\t"+ ru.getUrl());
//            AuthInterceptor.addRole(ru.getRoleId(), ru.getUrl());
//        }
        registry.addInterceptor(authInterceptor).addPathPatterns("/**");
    }
}
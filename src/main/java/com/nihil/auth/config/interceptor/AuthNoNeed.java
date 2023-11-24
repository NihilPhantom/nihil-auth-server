package com.nihil.auth.config.interceptor;

import java.lang.annotation.*;


@Target({ElementType.METHOD, ElementType.TYPE}) // 注解的范围是类、接口、枚举的方法上
@Retention(RetentionPolicy.RUNTIME)             // 被虚拟机保存，可用反射机制读取
@Documented
public @interface AuthNoNeed {
}

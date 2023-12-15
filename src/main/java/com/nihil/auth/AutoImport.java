package com.nihil.auth;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;

@ConditionalOnProperty(
        name = "nihil-auth.env",
        havingValue = "pro"
)
@ComponentScans({
        @ComponentScan(basePackages = "com.nihil.auth.config"),
        @ComponentScan(basePackages = "com.nihil.auth.service"),
        @ComponentScan(basePackages = "com.nihil.auth.mapper"),
        @ComponentScan(basePackages = "com.nihil.auth.controller"),
})

public class AutoImport {

}
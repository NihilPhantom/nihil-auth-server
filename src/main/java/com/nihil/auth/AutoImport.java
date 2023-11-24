package com.nihil.auth;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.sql.DataSource;

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
@MapperScan(basePackages = "com.nihil.auth.mapper")

public class AutoImport {
    @Autowired
    private DataSource dataSource;

    @Value("${nihil-auth.data-isolation}")
    private String dataIsolation;

    @Bean
    public SqlSessionFactory sqlSessionFactory() throws Exception {
        SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        sessionFactory.setDataSource(dataSource);
        sessionFactory.setMapperLocations(
                new PathMatchingResourcePatternResolver().
                        getResources("classpath:/META-INF/"+dataIsolation+"_mapper/*.xml"));
        return sessionFactory.getObject();
    }
}
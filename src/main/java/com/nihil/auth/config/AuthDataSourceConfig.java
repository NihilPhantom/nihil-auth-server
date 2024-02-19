package com.nihil.auth.config;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.sql.DataSource;
import java.util.Arrays;


@MapperScan(basePackages = "com.nihil.*.mapper", annotationClass = Mapper.class)
@Configuration
public class AuthDataSourceConfig {

    @Autowired
    private DataSource dataSource;

    @Value("${nihil-auth.data-isolation}")
    private String dataIsolation;


    @Bean
    public SqlSessionFactory sqlSessionFactory() throws Exception {
        SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        sessionFactory.setDataSource(dataSource);

        Resource[] resources1 = new PathMatchingResourcePatternResolver().
                getResources("classpath:/mapper/*.xml");
        Resource[] resources2 = new PathMatchingResourcePatternResolver().
                getResources("classpath:/META-INF/" + dataIsolation + "_mapper/*.xml");

        Resource[] allResources = Arrays.copyOf(resources1, resources1.length + resources2.length);
        System.arraycopy(resources2, 0, allResources, resources1.length, resources2.length);

        sessionFactory.setMapperLocations(allResources);
        sessionFactory.getObject().getConfiguration().setMapUnderscoreToCamelCase(true); // 开启下划线转小驼峰
        return sessionFactory.getObject();
    }
}
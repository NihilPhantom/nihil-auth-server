# NIHIL-AUTH-SERVER

## 简介

这是一个只需要导包，就能实现 Springboot 权限管理的项目，

它的核心思想是使用 SpringAutoConfiguration 【自动配置】的方法将用户管理的 Service 和 Controller 注入到 Spring 容器中，实现一键导入权限管理服务。

同时项目还将注入一个用户信息信息拦截器，对用户权限进行拦截，对非授权的资源访问返回403。

目前项目正在开发阶段，请期待我的正式发布，开发过程中的源码将被及时的更新到本仓库。

## 版本依赖

SpringBoot 3.x （开发使用 3.1.0）
Mysql 8.x     （开发使用 8.0.22）

## 工作完成情况
√ 资源管理
√ 角色管理
√ 用户管理
□ Redis 内存支持
□ Oauth2授权管理
□ 多租户

## 使用方法
1、运行 sql/nihil_auth.sql 在 Mysql 中创建数据表

2、将此项目作为一个【模块】引入到项目的 pom 文件中，斌且引入Nihil 系列通用的实体类和工具类，以下是一个项目的pom文件案例

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.nihil</groupId>
    <artifactId>orderfile-sever</artifactId>
    <packaging>pom</packaging>
    <version>1.0-SNAPSHOT</version>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.1.0</version>
        <relativePath/>
    </parent>

    <modules>
        <module>nihil-common-utils</module>
        <module>nihil-common-entity</module>
        <module>your-project</module>
        <module>nihil-auth-server</module>
    </modules>

    <properties>
        <maven.compiler.source>17</maven.compiler.source>
        <maven.compiler.target>17</maven.compiler.target>
        <spring-cloud.version>2022.0.3</spring-cloud.version>
        <spring-cloud-alibaba-version>2022.0.0.0-RC2</spring-cloud-alibaba-version>
    </properties>

    <!-- spring-cloud 依赖声明 -->
    <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>org.springframework.cloud</groupId>
                <artifactId>spring-cloud-dependencies</artifactId>
                <version>${spring-cloud.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>
</project>
```

3、在你的项目 `your-project` 中添加对 nihil-auth-server 的依赖 即可实现权限控制
```xml
<dependency>
    <groupId>com.nihil</groupId>
    <artifactId>nihil-auth-server</artifactId>
    <version>1.0-SNAPSHOT</version>
    <scope>compile</scope>
</dependency>
```

## 更新说明
2023-11-24：初始化项目，基础服务和前端页面 

## 贡献
欢迎对该仓库中的案例进行贡献！欢迎Pull Request。

任何形式的问题和咨询，请提交 issues，上号看到了，我就会回复。

欢迎任何形式的贡献和反馈。
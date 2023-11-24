# Nihil Auth Server

nihil 系列服务下的用户认证服务，对标的服务是 Spring Security，
Spring Security 提供了用户认证和授权功能，但是没有提供用户的管理功能。

实现动态的打包，根据 nihilauth.server.type 的不同，打出不同的 jar 包。



## 支持多种认证方式

允许用户通过配置 `nihilauth.server.type` 实现对认证方式的修改

1. 用户名密码登录 ： `nihilauth.server.type=passwd`
   在当前模式下 系统会寻找 `UserService` 下的 `bool CheckUser (Object userObject)` 校验用户是否登录。
   


## 开启RBAC模式

nihilauth.server.tenant= enable


## 多租户

nihilauth.server.tenant= enable

开启租户模式

认证：
>在当前状态下，系统缓存用户常用的N条租户信息，其余的存入数据库中，系统应该暴露 check(User, tenantId) 方法，
便于开发者进行租户查询。（缓存方法，次数，最近访问，最近访问次数）

授权：
>暴露getAuthority(User, tenantId) 方法获取用户在当前租户下的权限
   


## 默认的存储

### maps


### redis
clienttoken：clienttoken 是客户端向认证授权服务器发起请求时的凭证，
用户对客户端进行认证。在redis中存储时，使用client:[clienttoken] 的形式进行存储。
对应的内容是clientId。


    private static final String UserTokenKey = "user:";

    private static final String ClientTokenKey = "client:";


### application 属性

nihil-auth.clientId 客户端ID

nihil-auth.data-isolation 数据隔离级别 [none, column, table, database]

none 不进行数据库隔离，这种情况适用于微服务的 CAS 领域下 


## 
1、扩展性 和 开发效率 的矛盾
2、独立性 和 代码复用的矛盾（通过代码转化实现，就像less 和 sass），
由于独立代码转化为代码复用的难度较大，而代码复用转化为独立模块较为简单，所以才出现了less 和 sass

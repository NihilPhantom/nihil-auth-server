# 数据集库设计

## 数据实体

- 用户
- 角色
- 资源
- 权限
- 租户


## 数据表

用户（auth-user）：
id, username, password

角色（auth-role）：
id, name

权限（auth-authority）：
id, name

资源（auth-recource）：
id, path, method

租户（auth-tenant）:
id, name

用户-角色（auth-user-role）：
user_id, role_id

用户-权限（auth-user-authority）：
user_id, authority_id

权限-资源（auth-authority-recource）
authority_id, recource_id

租户-用户-角色（auth-tenant-user-role）：
tenant_id, user_id, role_id





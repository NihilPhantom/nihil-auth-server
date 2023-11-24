package com.nihil.auth.mapper;


import com.nihil.common.auth.AuthUser;
import com.nihil.auth.entity.AuthUserRoleKey;
import com.nihil.auth.pojo.AuthRoleUrl;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AuthMapper {
    /**
     * 获取所有 角色->资源url 的映射
     * @return 多对多的 {角色, 资源url}
     */
    List<AuthRoleUrl> getAllRoleUrl();

    /**
     * 根据用户名和密码获取用户权限
     * @note   如果用户通过，但是没有添加用户对应的角色，角色id 会为 **NULL**
     * @param  user 用户信息，这里会使用到 userName, password
     * @return 多对多 {用户id, 角色id}
     */
    List<AuthUserRoleKey> getUserRoleByUser(AuthUser user);

    List<AuthUserRoleKey> getRoleByUserId(Long userId);

    Integer deleteRoleUrl(@Param("roleId") Integer roleId, @Param("resIds") Integer[] resIds);
}

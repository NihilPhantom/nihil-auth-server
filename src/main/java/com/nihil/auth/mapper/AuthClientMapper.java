package com.nihil.auth.mapper;

import com.nihil.auth.entity.AuthClient;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface AuthClientMapper {

    @Select("SELECT * FROM auth_client WHERE id=#{id}")
    AuthClient getClientById (String id);
}

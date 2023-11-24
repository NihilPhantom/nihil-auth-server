package com.nihil.auth.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

@Mapper
@ConditionalOnProperty(name = "myapp.impl", havingValue = "impl1")
public interface TIUserMapper {

}

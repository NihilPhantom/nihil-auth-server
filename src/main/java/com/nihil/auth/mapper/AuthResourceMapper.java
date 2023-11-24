package com.nihil.auth.mapper;

import com.nihil.auth.entity.AuthResource;
import com.nihil.auth.pojo.AddUserResourceParam;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AuthResourceMapper {

    List<AuthResource> getResource();

    Integer addUserResource(AddUserResourceParam param);

    Integer addResource(AuthResource res);

    Integer addResToWhiteList(Long resId);

    Integer removeResFromWhiteList(Long resId);

    List<Long> getListByUid(Long uid);

    List<AuthResource> getDirectResourceByUid(Integer uid);

    Integer delResList(List<AuthResource> redundantResIdList);

    Integer addResList(List<AuthResource> addResList);

    Integer addUserResList(Long uid, List<Long> newResIdList);

    Integer delUserResList(Long uid, List<Long> needDeleteResIdList);
}

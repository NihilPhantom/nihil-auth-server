package com.nihil.auth.pojo;

import com.nihil.auth.entity.AuthResource;
import lombok.Data;

import java.util.List;

@Data
public class RoleResListParam {
    Integer roleId;
    List<Long> resIdList;
}

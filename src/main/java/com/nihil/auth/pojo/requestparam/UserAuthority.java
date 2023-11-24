package com.nihil.auth.pojo.requestparam;

import lombok.Data;

import java.util.List;

@Data
public class UserAuthority {
    List<Integer> roleList;
    List<Long> resList;
}

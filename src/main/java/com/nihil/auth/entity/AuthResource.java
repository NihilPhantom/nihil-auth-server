package com.nihil.auth.entity;


import lombok.Data;

@Data
public class AuthResource {
    Long id;
    String des;
    String method;
    String url;
    Boolean permanent;
}

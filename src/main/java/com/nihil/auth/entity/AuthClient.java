package com.nihil.auth.entity;


import lombok.Data;

@Data
public class AuthClient {

    String id;

    String secret;

//    String verifyType; // innerMemory, redis, jwt

}

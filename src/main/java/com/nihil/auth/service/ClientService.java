package com.nihil.auth.service;

import com.nihil.auth.entity.AuthClient;
import com.nihil.auth.exception.NihilAuthException;
import com.nihil.auth.mapper.AuthClientMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ClientService {

    @Resource
    AuthClientMapper clientMapper;

    @Resource
    TokenService redisTokenService;

    public String getClientToken(
            String clientId,
            String clientSecret
    ){
        // 检查客户端id 和 密码是否正确
        AuthClient authClient = clientMapper.getClientById(clientId);
        if(!authClient.getSecret().equals(clientSecret)){
            throw new NihilAuthException("客户端登录错误");
        }

        // 获取客户端token
        String token = redisTokenService.client2Token(authClient);
        return token;
    }

    public List<String> getRedirectList(String clientId){
        if(clientId.equals("1")){
            return new ArrayList<>(){{
                add("https://127.0.0.1/wnote/tool");
            }};
        }
        return null;
    }

    public String getCallback(String clientId){
        if(clientId.equals("1")){
            return "";
        }
        return null;
    }
}

package com.nihil.auth.controller;

import com.nihil.auth.config.interceptor.AuthNoNeed;
import com.nihil.auth.service.ClientService;
import com.nihil.common.auth.ClientLoginParam;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/client")
public class ClientController extends BaseController{

    @Resource
    ClientService clientService;

    /**
     * 客户端登录方法，此方法在登录成功后会返回一个 token 用以进行下一次的客户端认证
     * @param param {clientId, clientSecret}
     * @return 成功时返回token
     */
    @AuthNoNeed
    @PostMapping("/login")
    public String clientLogin (@RequestBody ClientLoginParam param) {
        String clientToken = clientService.getClientToken(param.getClientId(), param.getClientSecret());
        return clientToken;
    }
}

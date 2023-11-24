package com.nihil.auth.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.nihil.auth.entity.AuthClient;
import com.nihil.auth.pojo.Session;
import com.nihil.common.auth.AuthUser;

public interface TokenService {
    String user2Token(AuthUser user);
    Session token2Session(String token);

    void customSession(AuthUser user, Session session);

    String client2Token(AuthClient client);

    String token2ClientId(String session);
}

package com.nihil.auth;

public interface CodeService {
    void setCodeToken(String code, String token);
    String getToken(String code);

}

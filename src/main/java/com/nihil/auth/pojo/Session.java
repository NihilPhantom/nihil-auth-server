package com.nihil.auth.pojo;

import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class Session {

    String Id;
    Map<String, Object> info;

    List<Long> authorities;

    List<Integer> roles;

    public Session(){
        info = new HashMap<>();
    }

    public Object get(String s) {
        return this.info.get(s);
    }

    public void set(String s, Object o) {
        this.info.put(s, o);
    }

}

package com.nihil.auth.service;

import com.nihil.auth.CodeService;
import com.nihil.common.delayed.DelayedMap;
import jakarta.annotation.PostConstruct;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component("inMemoryCodeService")
@EnableScheduling
public class InMemoryCodeService implements CodeService {

    private DelayedMap<String, String> delayedMap;

    @PostConstruct
    public void init() {
        delayedMap = new DelayedMap<>();
    }

    @Override
    public void setCodeToken(String code, String token) {
        delayedMap.put(code, token, 10000); // 设置有效期为10秒
    }

    @Override
    public String getToken(String code) {
        return delayedMap.remove(code);
    }

    @Scheduled(fixedDelay = 2000) // 每秒执行一次
    public void removeExpiredItems() {
        delayedMap.removeExpiredItems();
    }
}

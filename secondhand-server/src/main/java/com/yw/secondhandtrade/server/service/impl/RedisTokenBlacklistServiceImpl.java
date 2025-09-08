package com.yw.secondhandtrade.server.service.impl;

import com.yw.secondhandtrade.server.service.RedisTokenBlacklistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RedisTokenBlacklistServiceImpl implements RedisTokenBlacklistService {

    private static final String BLACKLIST_PREFIX = "jwt:blacklist:";

    @Autowired
    private StringRedisTemplate redisTemplate;

    public boolean isTokenBlacklisted(String jti) {
        if (jti == null) {
            return false;
        }
        String key = BLACKLIST_PREFIX + jti;
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }
}

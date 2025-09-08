package com.yw.secondhandtrade.auth.service.impl;

import com.yw.secondhandtrade.auth.service.RedisTokenBlacklistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class RedisTokenBlacklistServiceImpl implements RedisTokenBlacklistService {
    private static final String BLACKLIST_PREFIX = "jwt:blacklist:";

    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * 将 JWT 的 JTI 加入黑名单
     * @param jti JWT 的唯一标识
     * @param expiresIn 剩余的过期时间（秒）
     */
    public void blacklistToken(String jti, long expiresIn) {
        String key = BLACKLIST_PREFIX + jti;
        redisTemplate.opsForValue().set(key, "revoked", Duration.ofSeconds(expiresIn));
    }

    /**
     * 检查 JTI 是否在黑名单中
     * @param jti JWT 的唯一标识
     * @return 如果在黑名单中则返回 true
     */
    public boolean isTokenBlacklisted(String jti) {
        String key = BLACKLIST_PREFIX + jti;
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }
}

package com.yw.secondhandtrade.auth.service;

public interface RedisTokenBlacklistService {
    /**
     * 将 JWT 的 JTI 加入黑名单
     * @param jti JWT 的唯一标识
     * @param expiresIn 剩余的过期时间（秒）
     */
    void blacklistToken(String jti, long expiresIn);

    /**
     * 检查 JTI 是否在黑名单中
     * @param jti JWT 的唯一标识
     * @return 如果在黑名单中则返回 true
     */
    boolean isTokenBlacklisted(String jti);
}

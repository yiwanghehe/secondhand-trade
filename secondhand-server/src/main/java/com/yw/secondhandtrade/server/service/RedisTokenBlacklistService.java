package com.yw.secondhandtrade.server.service;

public interface RedisTokenBlacklistService {
    boolean isTokenBlacklisted(String jti);
}

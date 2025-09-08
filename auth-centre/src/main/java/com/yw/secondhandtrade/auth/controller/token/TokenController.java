package com.yw.secondhandtrade.auth.controller.token;

import com.yw.secondhandtrade.auth.service.RedisTokenBlacklistService;
import com.yw.secondhandtrade.common.result.Result;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
@RequestMapping("/api/token")
@Tag(name = "登出")
public class TokenController {

    @Autowired
    private RedisTokenBlacklistService blacklistService;

    @PostMapping("/logout")
    public Result<?> logout(Authentication authentication) {
        if (authentication instanceof JwtAuthenticationToken) {
            Jwt jwt = ((JwtAuthenticationToken) authentication).getToken();
            String jti = jwt.getId();
            Instant expiresAt = jwt.getExpiresAt();

            if (jti != null && expiresAt != null) {
                long expiresIn = expiresAt.getEpochSecond() - Instant.now().getEpochSecond();
                if (expiresIn > 0) {
                    blacklistService.blacklistToken(jti, expiresIn);
                }
            }
            return Result.success("登出成功");
        }
        return Result.error("无效的登出请求");
    }
}

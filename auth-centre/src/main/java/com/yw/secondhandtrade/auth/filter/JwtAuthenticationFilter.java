package com.yw.secondhandtrade.auth.filter;

import com.yw.secondhandtrade.common.constant.JwtClaimsConstant;
import com.yw.secondhandtrade.common.constant.RoleConstant;
import com.yw.secondhandtrade.common.context.BaseContext;
import com.yw.secondhandtrade.common.properties.JwtProperties;
import com.yw.secondhandtrade.common.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

/**
 * JWT认证过滤器，用于替代原有的Interceptor
 * 这个过滤器会拦截所有需要认证的请求，校验JWT令牌，
 * 并将认证信息存入Spring Security的上下文中。
 */
@Component
@Slf4j
@Deprecated(since = "已经弃用")
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtProperties jwtProperties;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {
//
        String requestURI = request.getRequestURI();
        String token = null;
        Claims claims = null;

        try {
            // 区分管理端和用户端
            if (requestURI.startsWith("/admin/")) {
                // 处理管理端请求
                token = request.getHeader(jwtProperties.getAdminTokenName());
                if (StringUtils.hasText(token)) {
                    log.info("【管理端】JWT校验: {}", token);
                    claims = JwtUtil.parseJWT(jwtProperties.getAdminSecretKey(), token);
                    // 额外校验角色
                    Integer role = (Integer) claims.get(JwtClaimsConstant.USER_ROLE);
                    if (role == null || !role.equals(RoleConstant.ADMIN)) {
                        throw new SecurityException("管理员角色权限不足");
                    }
                }
            } else if (requestURI.startsWith("/user/")) {
                // 处理用户端请求
                token = request.getHeader(jwtProperties.getUserTokenName());
                if (StringUtils.hasText(token)) {
                    log.info("【用户端】JWT校验: {}", token);
                    // 用户端API允许用户或管理员的令牌访问
                    try {
                        // 优先尝试用户密钥
                        claims = JwtUtil.parseJWT(jwtProperties.getUserSecretKey(), token);
                    } catch (Exception e) {
                        log.warn("作为用户令牌解析失败，尝试作为管理员令牌解析...");
                        // 如果失败，尝试管理员密钥
                        claims = JwtUtil.parseJWT(jwtProperties.getAdminSecretKey(), token);
                    }
                }
            }

            // 如果成功解析出claims，则进行认证
            if (claims != null) {
                Long userId = Long.valueOf(claims.get(JwtClaimsConstant.USER_ID).toString());
                log.info("当前用户/管理员id: {}", userId);

                BaseContext.setId(userId);

                // 创建Spring Security的认证对象，并存入其安全上下文
                Integer role = (Integer) claims.get(JwtClaimsConstant.USER_ROLE);
                String authority = role.equals(RoleConstant.ADMIN) ? "ROLE_ADMIN" : "ROLE_USER";

                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                        userId, // principal: 通常是用户ID或用户名
                        null,   // credentials: 对于JWT认证，密码是null
                        Collections.singletonList(new SimpleGrantedAuthority(authority)) // authorities
                );
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        } catch (Exception e) {
            log.error("JWT令牌校验失败: {}", e.getMessage());
        }

        filterChain.doFilter(request, response);
        // 在请求结束后清理线程上下文，防止内存泄漏
        BaseContext.removeId();
    }
}

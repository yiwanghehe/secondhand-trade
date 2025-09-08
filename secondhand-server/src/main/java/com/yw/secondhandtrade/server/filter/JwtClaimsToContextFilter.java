package com.yw.secondhandtrade.server.filter;

import com.yw.secondhandtrade.common.constant.JwtClaimsConstant;
import com.yw.secondhandtrade.common.context.BaseContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;

/**
 * 一个自定义过滤器，在Spring Security认证之后执行。
 * <p>
 * 它的主要作用是，在每次HTTP请求中，从已通过认证的 {@link Authentication} 对象中
 * 提取出用户的唯一标识（userId），并将其设置到线程本地变量 {@link BaseContext} 中。
 * 这样，业务逻辑层的代码就可以方便地通过 {@code BaseContext.getId()} 获取当前操作的用户ID，
 * 而无需在每个方法中都传递用户ID参数。
 * <p>
 * 此过滤器能够处理两种主要的认证场景：
 * 1.  <b>无状态API调用</b>: 当客户端通过 Bearer Token 访问时，认证对象是 {@link JwtAuthenticationToken}。
 * 2.  <b>有状态浏览器会话</b>: 当用户通过浏览器登录（如SSO）时，认证对象是 {@link OAuth2AuthenticationToken}。
 */
@Component
@Slf4j
public class JwtClaimsToContextFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication != null) {
                Map<String, Object> claims = null;

                // 场景一：处理无状态API调用 (Bearer Token)，认证对象为 JwtAuthenticationToken
                if (authentication instanceof JwtAuthenticationToken) {
                    Jwt jwt = (Jwt) authentication.getPrincipal();
                    claims = jwt.getClaims();
                    log.info("检测到 JwtAuthenticationToken (API调用)，提取 Claims。");
                }
                // 场景二：处理有状态浏览器登录 (Session + Cookie)，认证对象为 OAuth2AuthenticationToken
                else if (authentication instanceof OAuth2AuthenticationToken) {
                    OAuth2AuthenticationToken oauth2Token = (OAuth2AuthenticationToken) authentication;
                    OAuth2User principal = oauth2Token.getPrincipal();
                    claims = principal.getAttributes();
                    log.info("检测到 OAuth2AuthenticationToken (浏览器登录)，提取 User Attributes。");
                }

                if (claims != null) {
                    Object userIdObj = claims.get(JwtClaimsConstant.USER_ID);
                    if (userIdObj instanceof Number) {
                        Long userId = ((Number) userIdObj).longValue();
                        log.info("从 Claims/Attributes 中提取用户ID: {} 并设置到BaseContext中", userId);
                        BaseContext.setId(userId);
                    }
                }
            }

            filterChain.doFilter(request, response);

        } finally {
            // 在请求处理完毕后，无论成功与否，都清理线程本地变量，防止内存泄漏
            BaseContext.removeId();
            log.info("请求处理完毕，已清理BaseContext");
        }
    }
}

package com.yw.secondhandtrade.server.config;

import com.yw.secondhandtrade.common.constant.RoleConstant;
import com.yw.secondhandtrade.server.filter.JwtClaimsToContextFilter;
import com.yw.secondhandtrade.server.service.RedisTokenBlacklistService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.oauth2.client.oidc.web.logout.OidcClientInitiatedLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority;
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import java.util.*;

/**
 * 资源服务器的安全配置类。
 */
@Configuration
@EnableWebSecurity
@Slf4j
public class SecurityConfiguration {

    @Autowired
    private JwtClaimsToContextFilter jwtClaimsToContextFilter;

    @Autowired
    private ClientRegistrationRepository clientRegistrationRepository;

    @Autowired
    private RedisTokenBlacklistService redisTokenBlacklistService;

    /**
     * 配置资源服务器的安全过滤器链。
     * @param http HttpSecurity配置对象。
     * @return 配置好的SecurityFilterChain实例。
     * @throws Exception 配置过程中可能抛出的异常。
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 禁用CSRF防护，这对于无状态的RESTful API是常见的做法
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize
                        // 放行公共接口、Swagger文档等
                        .requestMatchers(
                                "/common/**",
                                "/v3/api-docs/**",
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/doc.html",
                                "/webjars/**",
                                "/swagger-resources/**",
                                "/ws/chat/**"
                        ).permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/user/**").hasAnyRole("USER", "ADMIN") // 普通用户和管理员都能访问
                        // 其他所有请求都需要认证
                        .anyRequest().authenticated()
                )
                // 配置OAuth2登录流程
                .oauth2Login(login -> login
                        .userInfoEndpoint(userInfo -> userInfo
                                // 应用自定义的权限映射器，从用户信息中提取角色
                                .userAuthoritiesMapper(grantedAuthoritiesMapper())
                        ))
                .logout(logout -> logout
                        .logoutSuccessHandler(oidcLogoutSuccessHandler())) // 配置客户端发起的 OIDC 登出
                // 配置为OAuth2资源服务器，并指定JWT处理方式
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt
                        // 使用自定义的JWT认证转换器
                        .jwtAuthenticationConverter(jwtAuthenticationConverter())
                        // 使用自定义的JWT解码器（包含黑名单验证）
                        .decoder(jwtDecoder())));

        // 添加自定义过滤器，用于将JWT中的claims信息设置到BaseContext中
        http.addFilterAfter(jwtClaimsToContextFilter, AuthorizationFilter.class);

        return http.build();
    }

    /**
     * 创建一个 OIDC 客户端发起的登出成功处理器。
     * <p>
     * 这个处理器会自动从客户端注册信息中找到认证中心的登出端点，
     * 并将用户重定向到那里，实现单点登出（SSO Logout）。
     *
     * @return LogoutSuccessHandler 实例。
     */
    @Bean
    public LogoutSuccessHandler oidcLogoutSuccessHandler() {
        OidcClientInitiatedLogoutSuccessHandler successHandler =
                new OidcClientInitiatedLogoutSuccessHandler(this.clientRegistrationRepository);

        successHandler.setPostLogoutRedirectUri("http://localhost:8080/"); // 设置在认证中心登出成功后，最终重定向回的地址
        return successHandler;
    }

    /**
     * 为 OAuth2 登录流程创建一个 GrantedAuthoritiesMapper。
     * <p>
     * 它的作用是读取 OIDC/OAuth2 用户信息中的自定义 'role' 声明，
     * 并将其映射为 Spring Security 理解的 'ROLE_' 格式的权限。
     * 这对于通过第三方登录的用户进行角色授权至关重要。
     *
     * @return GrantedAuthoritiesMapper 实例。
     */
    @Bean
    public GrantedAuthoritiesMapper grantedAuthoritiesMapper() {
        return (authorities) -> {
            Set<GrantedAuthority> mappedAuthorities = new HashSet<>();

            authorities.forEach(authority -> {
                // 保留默认的权限 (如 OIDC_USER, SCOPE_...)
                mappedAuthorities.add(authority);

                Map<String, Object> claims = null;
                // 从OIDC用户的ID Token中获取claims
                if (authority instanceof OidcUserAuthority) {
                    claims = ((OidcUserAuthority) authority).getIdToken().getClaims();
                } else if (authority instanceof OAuth2UserAuthority) {
                    claims = ((OAuth2UserAuthority) authority).getAttributes();
                }

                if (claims != null) {
                    log.debug("【Server DEBUG】在 GrantedAuthoritiesMapper 中解析 Claims: {}", claims);
                    Object roleObj = claims.get("role");
                    if (roleObj != null) {
                        // 转换为Integer进行比较
                        Integer role = ((Number) roleObj).intValue();
                        if (role.equals(RoleConstant.ADMIN)) {
                            mappedAuthorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
                            log.debug("【Server DEBUG】映射权限: ROLE_ADMIN");
                        } else if (role.equals(RoleConstant.USER)) {
                            mappedAuthorities.add(new SimpleGrantedAuthority("ROLE_USER"));
                            log.debug("【Server DEBUG】映射权限: ROLE_USER");
                        }
                    }
                }

            });

            return mappedAuthorities;
        };
    }

    /**
     * 自定义JWT认证转换器。
     * <p>
     * 当资源服务器接收到Bearer Token时，此转换器负责从JWT的claims中提取角色信息，
     * 并将其转换为Spring Security的GrantedAuthority集合，以便进行后续的授权判断。
     *
     * @return JwtAuthenticationConverter 实例。
     */
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        Converter<Jwt, Collection<GrantedAuthority>> grantedAuthoritiesConverter = jwt -> {
            log.debug("【Server DEBUG】进入 jwtAuthenticationConverter...");
            log.debug("【Server DEBUG】收到的JWT Claims: {}", jwt.getClaims());

            Object roleObj = jwt.getClaim("role");
            if (roleObj == null) {
                log.warn("【Server DEBUG】JWT 中未找到 'role' claim，返回空权限。");
                return Collections.emptyList();
            }

            Integer role = ((Number) roleObj).intValue();
            log.debug("【Server DEBUG】从JWT中提取到 role: {}", role);

            if (role.equals(RoleConstant.ADMIN)) {
                log.debug("【Server DEBUG】转换为权限: ROLE_ADMIN");
                return List.of(new SimpleGrantedAuthority("ROLE_ADMIN"));
            } else if (role.equals(RoleConstant.USER)) {
                log.debug("【Server DEBUG】转换为权限: ROLE_USER");
                return List.of(new SimpleGrantedAuthority("ROLE_USER"));
            }

            log.warn("【Server DEBUG】role 值 {} 无效，返回空权限。", role);
            return Collections.emptyList();
        };

        JwtAuthenticationConverter jwtConverter = new JwtAuthenticationConverter();
        // 将上述转换器设置到JWT认证转换器中
        jwtConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
        return jwtConverter;
    }

    /**
     * 创建一个自定义的 JwtDecoder，它包含了标准的验证逻辑以及我们自定义的黑名单验证。
     *
     * @return JwtDecoder 实例。
     */
    @Bean
    public JwtDecoder jwtDecoder() {
        String issuerUri = "http://localhost:8081"; // 确保这和 auth-centre 配置一致
        NimbusJwtDecoder jwtDecoder = JwtDecoders.fromIssuerLocation(issuerUri);

        // 标准验证器，负责检查 a.o. issuer, audience, timestamp
        OAuth2TokenValidator<Jwt> defaultValidator = JwtValidators.createDefaultWithIssuer(issuerUri);

        // 自定义黑名单验证器，检查令牌ID是否存在于Redis黑名单中
        OAuth2TokenValidator<Jwt> blacklistValidator = token -> {
            if (redisTokenBlacklistService.isTokenBlacklisted(token.getId())) {
                return OAuth2TokenValidatorResult.failure(
                        new OAuth2Error("invalid_token", "该令牌已被吊销", null)
                );
            }
            return OAuth2TokenValidatorResult.success();
        };

        // 将多个验证器合并成一个委托验证器
        jwtDecoder.setJwtValidator(new DelegatingOAuth2TokenValidator<>(defaultValidator, blacklistValidator));

        return jwtDecoder;
    }

}

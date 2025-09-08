package com.yw.secondhandtrade.auth.config;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import com.yw.secondhandtrade.auth.mapper.UserMapper;
import com.yw.secondhandtrade.common.constant.JwtClaimsConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;
import com.yw.secondhandtrade.pojo.entity.User;
import org.springframework.security.core.Authentication;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.UUID;


/**
 * Spring Authorization Server 配置类.
 * <p>
 * 此类用于配置 OAuth2 授权服务器，包括客户端注册、令牌自定义和 JWT 签名密钥管理。
 */
@Configuration
@Slf4j
public class AuthorizationServerConfig {

    @Autowired
    private UserMapper userMapper;

    /**
     * 配置并注册OAuth 2.0客户端信息。
     * <p>
     * Spring Authorization Server 使用 {@link RegisteredClientRepository} 来管理客户端信息。
     * 这里我们使用内存存储 {@link InMemoryRegisteredClientRepository} 来注册一个客户端。
     *
     * @return RegisteredClientRepository 客户端仓库实例。
     */
    @Bean
    public RegisteredClientRepository registeredClientRepository() {
        RegisteredClient oidcClient = RegisteredClient.withId(UUID.randomUUID().toString())
                .clientId("secondhand-client") // 客户端ID
                .clientSecret("{noop}secret") // 客户端密钥, {noop}表示不加密, 生产环境必须加密
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC) // 认证方式
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE) // 授权码模式
                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN) // 刷新令牌
                .redirectUri("http://localhost:8080/login/oauth2/code/secondhand-client") // secondhand-server 回调地址
                .redirectUri("https://www.apifox.cn/oauth2/callback") // 为 apifox 测试添加的回调地址
                .postLogoutRedirectUri("http://localhost:8080/") // 登出后重定向地址
                .scope(OidcScopes.OPENID) // OIDC范围
                .scope(OidcScopes.PROFILE) // OIDC范围
                .scope("read") // 自定义范围
                .scope("write") // 自定义范围
                .clientSettings(ClientSettings.builder().requireAuthorizationConsent(true).build()) // 配置客户端设置，例如：是否需要用户授权确认页面
                .build();

        return new InMemoryRegisteredClientRepository(oidcClient);
    }

    /**
     * 自定义 JWT Claims。
     * <p>
     * 这个 Bean 允许我们在生成 JWT 时向其中添加自定义的声明。
     * 这里我们将用户的ID和角色信息添加到JWT中，以便资源服务器可以利用这些信息进行细粒度的访问控制。
     *
     * @return OAuth2TokenCustomizer 实例。
     */
    @Bean
    public OAuth2TokenCustomizer<JwtEncodingContext> tokenCustomizer() {
        return context -> {
            log.debug("【AuthCentre DEBUG】进入 tokenCustomizer...");
            Authentication principal = context.getPrincipal();
            if (principal != null) {
                String username = principal.getName();
                log.debug("【AuthCentre DEBUG】获取到 principal, 用户名: {}", username);
                User user = userMapper.getByUsername(username);

                if (user != null) {
                    log.debug("【AuthCentre DEBUG】数据库查询成功, User ID: {}, Role: {}", user.getId(), user.getRole());
                    context.getClaims().claim(JwtClaimsConstant.USER_ID, user.getId());
                    context.getClaims().claim(JwtClaimsConstant.USER_ROLE, user.getRole());
                    log.debug("【AuthCentre DEBUG】已向JWT添加自定义 claims。");
                } else {
                    log.warn("【AuthCentre DEBUG】根据用户名 {} 未在数据库中找到用户！", username);
                }
            } else {
                log.warn("【AuthCentre DEBUG】无法获取 principal 对象！");
            }
        };
    }

    /**
     * 配置JWT签名密钥源 (JWKSource)。
     * <p>
     * JWK (JSON Web Key) 是一个标准格式，用于表示加密密钥。
     * JWKSource 负责提供用于签署JWT的密钥。这里我们动态生成一个RSA密钥对，并将其封装为JWKSource。
     * 在生产环境中，密钥应从安全的密钥库（Keystore）中加载，而不是每次启动时重新生成。
     *
     * @return JWKSource<SecurityContext> 实例。
     */
    @Bean
    public JWKSource<SecurityContext> jwkSource() {
        KeyPair keyPair = generateRsaKey();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        RSAKey rsaKey = new RSAKey.Builder(publicKey)
                .privateKey(privateKey)
                .keyID(UUID.randomUUID().toString())
                .build();
        JWKSet jwkSet = new JWKSet(rsaKey);
        return new ImmutableJWKSet<>(jwkSet);
    }

    /**
     * 生成RSA密钥对的辅助方法。
     *
     * @return KeyPair 包含公钥和私钥的密钥对。
     * @throws IllegalStateException 如果生成密钥失败。
     */
    private static KeyPair generateRsaKey() {
        KeyPair keyPair;
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            keyPair = keyPairGenerator.generateKeyPair();
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
        return keyPair;
    }

    /**
     * 配置JWT解码器 (JwtDecoder)。
     * <p>
     * 当授权服务器也作为资源服务器时（例如，在校验Token时），需要一个JwtDecoder来验证和解码JWT。
     * 这个解码器使用与签名时相同的 JWKSource 来获取公钥进行验签。
     *
     * @param jwkSource 用于获取验签公钥的JWKSource。
     * @return JwtDecoder 实例。
     */
    @Bean
    public JwtDecoder jwtDecoder(JWKSource<SecurityContext> jwkSource) {
        return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource);
    }

    /**
     * 配置授权服务器的全局设置。
     * <p>
     * AuthorizationServerSettings 用于配置授权服务器的端点URL、签发者(issuer)等信息。
     * issuer URI 是 OIDC 规范中的一个重要部分，它作为授权服务器的唯一标识符。
     *
     * @return AuthorizationServerSettings 实例。
     */
    @Bean
    public AuthorizationServerSettings authorizationServerSettings() {
        return AuthorizationServerSettings.builder()
                .issuer("http://localhost:8081") // 设置签发者URI
                .build();
    }
}

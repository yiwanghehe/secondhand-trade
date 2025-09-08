package com.yw.secondhandtrade.auth.config;

import com.yw.secondhandtrade.auth.handler.OAuth2LoginSuccessHandler;
import com.yw.secondhandtrade.auth.mapper.UserMapper;
import com.yw.secondhandtrade.auth.service.impl.CustomOAuth2UserService;
import com.yw.secondhandtrade.common.constant.RoleConstant;
import com.yw.secondhandtrade.pojo.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;

import java.util.Collections;

/**
 * Spring Security 主配置类。
 * <p>
 * 该类包含多个 SecurityFilterChain Bean，用于处理不同的安全场景。
 * {@link Order} 注解用于指定过滤器链的优先级，数字越小优先级越高。
 */
@Configuration
@EnableWebSecurity
@Slf4j
public class SecurityConfiguration {

    @Autowired
    private OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;

    @Autowired
    private CustomOAuth2UserService customOAuth2UserService;

    /**
     * 过滤器链1: 用于处理OAuth 2.0授权服务器的协议端点。
     * <p>
     * 这个过滤器链的优先级最高 (Order=1)，专门用于保护和处理OAuth 2.0的端点，
     * 例如 /oauth2/authorize, /oauth2/token 等。
     * 它应用了Spring Authorization Server的默认安全配置。
     *
     * @param http HttpSecurity 配置对象。
     * @return SecurityFilterChain 实例。
     * @throws Exception 配置过程中可能抛出的异常。
     */
    @Bean
    @Order(1)
    public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http) throws Exception {
        OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http);
        http.getConfigurer(OAuth2AuthorizationServerConfigurer.class)
                .oidc(Customizer.withDefaults());
        http.exceptionHandling((exceptions) -> exceptions
                        // 当用户未认证访问受保护资源时，重定向到 /login 页面
                        .authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/login")))
                // 配置资源服务器属性，使其能够验证收到的JWT
                .oauth2ResourceServer((resourceServer) -> resourceServer
                        .jwt(Customizer.withDefaults()));

        return http.build();
    }

    /**
     * 过滤器链2: 用于处理普通的用户认证和Web应用安全。
     * <p>
     * 这个过滤器链优先级较低 (Order=2)，负责处理所有其他请求，包括标准登录页面、
     * 业务API接口、以及第三方OAuth2登录（如GitHub）。
     *
     * @param http HttpSecurity 配置对象。
     * @return SecurityFilterChain 实例。
     * @throws Exception 配置过程中可能抛出的异常。
     */
    @Bean
    @Order(2)
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 配置请求授权规则
                .authorizeHttpRequests(authorize -> authorize
                        // 放行以下公共路径，无需认证即可访问
                        .requestMatchers(
                                "/common/**",
                                "/user/user/login",
                                "/user/user/register",
                                "/admin/user/login",
                                "/v3/api-docs/**",      // OpenAPI v3 文档
                                "/swagger-ui.html",     // Swagger UI 页面
                                "/swagger-ui/**",       // Swagger UI 静态资源
                                "/doc.html",            // Knife4j UI 页面
                                "/webjars/**",          // Webjars 静态资源
                                "/swagger-resources/**",// Swagger 资源
                                "/ws/chat/**",          // WebSocket 连接
                                "/login/**",            // 登录页面及相关资源
                                "/error"                // 默认错误页面
                        ).permitAll()
                        // OAuth2 登录相关的路径也需要放行
                        .requestMatchers("/login/oauth2/code/**", "/oauth2/**").permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN") // 访问/admin/**路径需要ADMIN角色
                        .requestMatchers("/user/**").authenticated()   // 访问/user/**路径只需要已认证即可（用户或管理员）
                        .requestMatchers("/api/token/logout").authenticated() // 允许已认证用户访问新的登出API
                        // 其他所有请求都需要认证
                        .anyRequest().authenticated()
                )
                // 启用并配置表单登录，使用默认的登录页面和处理逻辑
                .formLogin(Customizer.withDefaults())
                // 添加并配置 OAuth2 登录功能 (例如，使用GitHub登录)
                .oauth2Login(oauth2Login -> oauth2Login
                        // 配置获取用户信息的端点
                        .userInfoEndpoint(userInfo -> userInfo
                                // 指定自定义的 User Service 来处理从第三方获取的用户信息
                                .userService(customOAuth2UserService))
                        // 指定登录成功后的处理器
                        .successHandler(oAuth2LoginSuccessHandler))
                // 配置登出功能
                .logout(logout -> logout
                        // 登出成功后重定向到首页
                        .logoutSuccessUrl("http://localhost:8080/"))
                // 为本过滤器链保护的API接口配置资源服务器属性，使其能够解析和验证JWT
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));

        return http.build();
    }

    /**
     * 定义 UserDetailsService Bean，用于从数据库加载用户信息。
     * <p>
     * 这是Spring Security进行身份认证的核心接口。当用户尝试通过用户名密码登录时，
     * Spring Security会调用这个实现类的 {@code loadUserByUsername} 方法来获取用户信息进行比对。
     *
     * @return UserDetailsService 的一个实现实例。
     */
    @Bean
    public UserDetailsService userDetailsService() {
        class UserDetailsServiceImpl implements UserDetailsService {

            @Autowired
            private UserMapper userMapper;

            @Override
            public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
                log.info("尝试登录的用户: {}", username);
                User user = userMapper.getByUsername(username);
                if (user == null) {
                    log.warn("找不到用户名: {}", username);
                    throw new UsernameNotFoundException("找不到用户名: " + username);
                }

                // 将数据库中的角色字符串映射为Spring Security需要的 'ROLE_' 前缀格式
                String role = user.getRole().equals(RoleConstant.ADMIN) ? "ROLE_ADMIN" : "ROLE_USER";

                // Spring Security 默认使用 BCrypt, 而我们数据库是 MD5。
                // 为了兼容，我们在密码前加上 {noop} 表示不加密直接比对，或者使用 DelegatingPasswordEncoder
                // 这里，我们返回 UserDetails 对象，Spring Security 会处理密码校验
                // 注意：MD5是不安全的，生产环境强烈建议迁移到Bcrypt
                String encodedPassword = "{MD5}"+user.getPassword();

                return new org.springframework.security.core.userdetails.User(
                        user.getUsername(),
                        encodedPassword,
                        Collections.singletonList(new SimpleGrantedAuthority(role))
                );
            }
        }

        return new UserDetailsServiceImpl();
    }

    /**
     * 定义密码编码器 (PasswordEncoder) Bean。
     * <p>
     * 使用 {@link PasswordEncoderFactories#createDelegatingPasswordEncoder()} 可以创建一个
     * "委托"模式的密码编码器。它能够处理多种密码编码格式（通过密码字符串的前缀如 {bcrypt}, {MD5}, {noop} 来识别）。
     * 这在迁移旧的密码存储格式或支持多种密码算法时非常有用。
     *
     * @return PasswordEncoder 实例。
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        // 使用 DelegatingPasswordEncoder，它可以根据密码的前缀（如{bcrypt}, {MD5}）选择不同的编码器
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}

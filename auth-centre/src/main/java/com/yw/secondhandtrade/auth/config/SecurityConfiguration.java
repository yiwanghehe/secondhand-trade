package com.yw.secondhandtrade.auth.config;

import com.yw.secondhandtrade.auth.filter.JwtAuthenticationFilter;
import com.yw.secondhandtrade.auth.handler.OAuth2LoginSuccessHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    @Autowired
    private OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter; // JWT过滤器

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 禁用 CSRF，因为我们是无状态的API服务
                .csrf(csrf -> csrf.disable())

                // 设置会话管理策略为无状态 (STATELESS)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 配置请求授权规则
                .authorizeHttpRequests(authorize -> authorize
                        // 放行公共接口、登录注册、Swagger文档、WebSocket等
                        .requestMatchers(
                                "/common/**",
                                "/user/user/login",
                                "/user/user/register",
                                "/admin/user/login",
                                "/v3/api-docs/**",
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/doc.html", // 放行knife4j文档
                                "/webjars/**",           // 放行静态资源
                                "/swagger-resources/**", // 放行Swagger资源
                                "/ws/chat/**"
                        ).permitAll()
                        // OAuth2 登录相关的路径也需要放行
                        .requestMatchers("/login/oauth2/code/**", "/oauth2/**").permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN") // 访问/admin/**路径需要ADMIN角色
                        .requestMatchers("/user/**").authenticated()   // 访问/user/**路径只需要已认证即可（用户或管理员）
                        // 其他所有请求都需要认证
                        .anyRequest().authenticated()
                )

                // 配置 OAuth2 登录
                .oauth2Login(oauth2 -> oauth2
                        // 指定登录成功后的处理器
                        .successHandler(oAuth2LoginSuccessHandler)
                );

        // 将JWT过滤器添加到过滤器链中
        // 放在UsernamePasswordAuthenticationFilter之前
        // 因为对于API请求，先检查JWT，而不是用户名密码
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}

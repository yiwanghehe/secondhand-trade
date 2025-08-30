package com.yw.secondhandtrade.config;

import com.yw.secondhandtrade.interceptor.JwtTokenAdminInterceptor;
import com.yw.secondhandtrade.interceptor.JwtTokenUserInterceptor;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 适配 Spring Boot 3.x 的 WebMvc 配置，使用 Knife4j 4.x (OpenAPI 3)
 */
@Configuration
@Slf4j
public class WebMvcConfiguration implements WebMvcConfigurer {

    /**
     * 配置 OpenAPI 信息
     * @return OpenAPI 对象
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("二手交易平台项目接口文档")
                        .version("2.0")
                        .description("二手交易平台项目接口文档"));
    }

    // 在 Knife4j 4.x 中，不再需要单独的 Docket Bean，通过 @Tag 和 @Operation 注解自动分组。
    // 更细粒度的分组，可以使用 @GroupedOpenApi 注解。
    // 这里提供了两个示例，展示了如何根据包路径进行分组。
    // 根据需要选择性地启用它们。

    //@Bean
    //public GroupedOpenApi userApi() {
    //    return GroupedOpenApi.builder()
    //            .group("用户端接口")
    //            .pathsToMatch("/api/user/**")
    //            .build();
    //}

    //@Bean
    //public GroupedOpenApi adminApi() {
    //    return GroupedOpenApi.builder()
    //            .group("管理端接口")
    //            .pathsToMatch("/api/admin/**")
    //            .build();
    //}

    @Autowired
    private JwtTokenAdminInterceptor jwtTokenAdminInterceptor;

    @Autowired
    private JwtTokenUserInterceptor jwtTokenUserInterceptor;

    /**
     * 注册自定义拦截器
     *
     * @param registry
     */
    public void addInterceptors(InterceptorRegistry registry) {
        log.info("注册自定义拦截器...");

        registry.addInterceptor(jwtTokenAdminInterceptor)
                .addPathPatterns("/admin/**")
                .excludePathPatterns("/admin/employee/save")
                .excludePathPatterns("/admin/employee/login");

        registry.addInterceptor(jwtTokenUserInterceptor)
                .addPathPatterns("/user/**")
                .excludePathPatterns("/user/user/save")
                .excludePathPatterns("/user/user/login")
                .excludePathPatterns("/user/shop/status");
    }
}

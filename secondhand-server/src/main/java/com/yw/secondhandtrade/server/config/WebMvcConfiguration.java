package com.yw.secondhandtrade.server.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@Slf4j
public class WebMvcConfiguration implements WebMvcConfigurer {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("二手交易平台项目接口文档")
                        .version("2.0")
                        .description("二手交易平台项目接口文档"));
    }

//    @Autowired
//    private JwtTokenAdminInterceptor jwtTokenAdminInterceptor;
//
//    @Autowired
//    private JwtTokenUserInterceptor jwtTokenUserInterceptor;
//
//    /**
//     * 注册自定义拦截器
//     */
//    @Override
//    public void addInterceptors(InterceptorRegistry registry) {
//        log.info("注册自定义拦截器...");
//
//        // 管理端拦截器
//        registry.addInterceptor(jwtTokenAdminInterceptor)
//                .addPathPatterns("/admin/**")
//                .excludePathPatterns("/admin/user/login"); // 只放行管理员登录
//
//        // 用户端拦截器
//        registry.addInterceptor(jwtTokenUserInterceptor)
//                .addPathPatterns("/user/**")
//                .excludePathPatterns("/user/user/login") // 放行用户登录
//                .excludePathPatterns("/user/user/register"); // 放行用户注册
//    }
}


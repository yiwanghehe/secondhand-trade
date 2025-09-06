package com.yw.secondhandtrade.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ComponentScan;

/*
在 secondhand-common 模块的 src/main/resources/META-INF/spring 目录下，
创建一个名为 org.springframework.boot.autoconfigure.AutoConfiguration.imports 的文件
（如果使用的是 Spring Boot 2.7.x 或更早版本，则创建 spring.factories 文件）。
在org.springframework.boot.autoconfigure.AutoConfiguration.imports 文件中，添加com.yw.secondhandtrade.common.config.CommonAutoConfiguration
secondhand-common模块就成为了一个 Spring Boot “Starter” 模块。当其他模块引入 secondhand-common 的依赖后，
Spring Boot 会自动发现并加载 CommonAutoConfiguration 类，从而将 secondhand-common 模块中的所有 Bean 注册到容器中，无需任何额外配置。
 */
@Configuration
@ComponentScan("com.yw.secondhandtrade.common") // 扫描 common 模块中的所有组件
public class CommonAutoConfiguration {
    // 可以在这里定义Bean
}

package com.yw.secondhandtrade.auth;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
@MapperScan("com.yw.secondhandtrade.auth.mapper")
public class AuthCentreApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthCentreApplication.class, args);
    }
}

package com.yw.secondhandtrade;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
@MapperScan("com.yw.secondhandtrade.mapper")
public class SecondhandTradeApplication {

    public static void main(String[] args) {
        SpringApplication.run(SecondhandTradeApplication.class, args);
    }

}

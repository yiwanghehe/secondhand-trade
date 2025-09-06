package com.yw.secondhandtrade.server.config;

import com.yw.secondhandtrade.common.properties.AliOSSProperties;
import com.yw.secondhandtrade.common.utils.AliOSSUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;


@Configuration
@Slf4j
public class OSSConfiguration {

    @Bean
    @ConditionalOnMissingBean
    @Lazy // 懒加载，第一次使用时再向阿里那边发送请求创建实例
    public AliOSSUtils aliOSSUtils(AliOSSProperties aliOSSProperties){
        log.info("创建OSSClient实例");
        return new AliOSSUtils(
                aliOSSProperties.getEndpoint(),
                aliOSSProperties.getAccessKeyId(),
                aliOSSProperties.getAccessKeySecret(),
                aliOSSProperties.getBucketName());
    }
}

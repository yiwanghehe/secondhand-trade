package com.yw.secondhandtrade.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class SerializationConfiguration {

    // 使用StringRedisSerializer来序列化和反序列化redis的key值
    @Bean("keySerializer")
    public RedisSerializer<String> keySerializer() {
        return new StringRedisSerializer();
    }

    // 使用GenericJackson2JsonRedisSerializer来序列化和反序列化redis的value值
    // 默认配置下的Jackson不认识Java 8新引入的LocalDateTime这种日期时间类型
    // Spring Boot的Web层（Spring MVC）已经有了一个配置好的、支持LocalDateTime的ObjectMapper,所以注入jackson里即可
    @Bean("valueSerializer")
    public RedisSerializer<Object> valueSerializer(ObjectMapper objectMapper) {
        // 具体的实现，使用Jackson
        return new GenericJackson2JsonRedisSerializer(objectMapper);
    }

}

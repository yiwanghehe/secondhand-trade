package com.yw.secondhandtrade.server.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
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
        // 克隆一个全新的ObjectMapper实例
        // 这么做是为了不修改Spring Boot自动配置的全局ObjectMapper，避免影响MVC层的JSON处理
        ObjectMapper newObjectMapper = objectMapper.copy();

        // 对克隆出来的实例进行自定义配置
        newObjectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);

        // 启用默认类型信息，使其在序列化时包含@class属性
        newObjectMapper.activateDefaultTyping(
                LaissezFaireSubTypeValidator.instance,
                ObjectMapper.DefaultTyping.NON_FINAL,
                JsonTypeInfo.As.PROPERTY
        );

        // 使用配置好的 newObjectMapper 创建序列化器
        return new GenericJackson2JsonRedisSerializer(newObjectMapper);
    }

}

package com.yw.secondhandtrade.config.testrunner;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class RedisConnectionTestRunner implements CommandLineRunner {

    private boolean isConnected = false;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public void run(String... args) throws Exception {
        testConnection();
    }

    private void testConnection(){
        while(!isConnected){
            try {
                // 获取一个原生连接
                String pong = redisTemplate.getConnectionFactory().getConnection().ping();
                log.info("成功连接到 Redis！PING 响应: {}, {}", pong, getRedisConnectionInfo());
                isConnected = true;
            } catch (Exception e) {
                log.error("无法连接到 Redis！请检查配置和 Redis 服务状态。");
                log.error("错误信息: ", e);

                log.info("5秒后重试连接Redis: {}", getRedisConnectionInfo());
                try{
                    Thread.sleep(5000);
                } catch (InterruptedException ie){
                    Thread.currentThread().interrupt();
                    break;
                }

            }
        }

    }

    // 在类中添加属性来获取 Redis 连接信息
    private String getRedisConnectionInfo() {
        try {
            // 获取 Redis 连接工厂的具体实现
            LettuceConnectionFactory factory = (LettuceConnectionFactory) redisTemplate.getConnectionFactory();
            String host = factory.getHostName();
            int port = factory.getPort();
            return host + ":" + port;
        } catch (Exception e) {
            return "unknown";
        }
    }
}

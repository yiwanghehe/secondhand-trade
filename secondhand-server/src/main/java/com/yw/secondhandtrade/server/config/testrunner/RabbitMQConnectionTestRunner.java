package com.yw.secondhandtrade.server.config.testrunner;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.Connection;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.rabbitmq.client.Channel;

@Component
@Slf4j
public class RabbitMQConnectionTestRunner implements CommandLineRunner {

    private boolean isConnected = false;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Override
    public void run(String... args) throws Exception {
        log.info("开始检查 RabbitMQ 连接...");
        testConnection();
    }

    private void testConnection() {
        while (!isConnected) {
            try {
                // RabbitMQ 的连接测试可以通过尝试创建一个连接和一个通道来完成。
                // 如果成功，说明 broker 是可达的。
                // 使用 try-with-resources 确保连接和通道被自动关闭。
                ConnectionFactory connectionFactory = rabbitTemplate.getConnectionFactory();
                try (Connection connection = connectionFactory.createConnection();
                     Channel channel = connection.createChannel(false)) {

                    log.info("成功连接到 RabbitMQ！Broker: {}", getRabbitMQConnectionInfo());
                    isConnected = true;
                }
            } catch (Exception e) {
                log.error("无法连接到 RabbitMQ！请检查配置和 RabbitMQ 服务状态。");
                log.error("错误信息: ", e);

                log.info("5秒后重试连接 RabbitMQ: {}", getRabbitMQConnectionInfo());
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    log.warn("RabbitMQ 连接重试被中断。");
                    break;
                }
            }
        }
    }

    private String getRabbitMQConnectionInfo() {
        try {
            ConnectionFactory connectionFactory = rabbitTemplate.getConnectionFactory();
            String host = connectionFactory.getHost();
            int port = connectionFactory.getPort();
            return host + ":" + port;
        } catch (Exception e) {
            log.warn("获取 RabbitMQ 连接信息失败。", e);
            return "unknown";
        }
    }
}

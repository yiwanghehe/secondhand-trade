package com.yw.secondhandtrade.server.mq.sender;

import com.yw.secondhandtrade.common.constant.RabbitMQConstant;
import com.yw.secondhandtrade.pojo.dto.OrderMessageDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Slf4j
public class RabbitMQSender {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 发送订单创建消息到延迟队列，用于处理订单超时
     * @param orderId 订单ID
     */
    public void sendOrderToDelayQueue(Long orderId) {
        log.info("准备发送订单ID: {} 到延迟队列", orderId);
        // 使用CorrelationData来确保消息的唯一性，便于问题排查
        CorrelationData correlationData = new CorrelationData(UUID.randomUUID().toString());
        rabbitTemplate.convertAndSend(
                RabbitMQConstant.ORDER_EVENT_EXCHANGE,
                RabbitMQConstant.ORDER_CREATE_ROUTING_KEY,
                orderId,
                correlationData
        );
        log.info("订单ID: {} 已成功发送到延迟队列", orderId);
    }

    /**
     * 发送订单创建成功消息，用于异步通知
     * @param messageDTO 包含订单信息的消息DTO
     */
    public void sendOrderCreationNotification(OrderMessageDTO messageDTO) {
        log.info("准备发送订单创建成功通知: {}", messageDTO);
        CorrelationData correlationData = new CorrelationData(UUID.randomUUID().toString());
        rabbitTemplate.convertAndSend(
                RabbitMQConstant.ORDER_NOTIFY_TOPIC_EXCHANGE,
                RabbitMQConstant.ORDER_NOTIFY_ROUTING_KEY,
                messageDTO,
                correlationData
        );
        log.info("订单创建成功通知已发送: {}", messageDTO);
    }
}

package com.yw.secondhandtrade.mq.listener;

import com.rabbitmq.client.Channel;
import com.yw.secondhandtrade.common.constant.OrderStatusConstant;
import com.yw.secondhandtrade.common.constant.RabbitMQConstant;
import com.yw.secondhandtrade.mapper.OrdersMapper;
import com.yw.secondhandtrade.pojo.entity.Orders;
import com.yw.secondhandtrade.service.OrdersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

@Component
@Slf4j
public class OrderMessageListener {

    @Autowired
    private OrdersService ordersService;

    @Autowired
    private OrdersMapper ordersMapper;

    /**
     * 监听死信队列，处理超时的订单
     * @param orderId 订单ID
     * @param message RabbitMQ消息对象
     * @param channel AMQP通道
     * @throws IOException
     */
    @RabbitListener(queues = RabbitMQConstant.ORDER_DEAD_QUEUE)
    @Transactional
    public void handleTimeoutOrder(Long orderId, Message message, Channel channel) throws IOException {
        long deliveryTag = message.getMessageProperties().getDeliveryTag();

        try {
            log.info("收到超时订单消息，订单ID: {}", orderId);
            Orders order = ordersMapper.getById(orderId);

            if (order == null) {
                log.warn("订单ID: {} 不存在，可能已被删除。消息将被确认。", orderId);
                channel.basicAck(deliveryTag, false);
                return;
            }

            // 核心逻辑：只有当订单状态仍为“待付款”时，才执行取消操作
            if (OrderStatusConstant.PENDING_PAYMENT.equals(order.getStatus())) {
                log.info("订单ID: {} 状态为待付款，执行自动取消操作。", orderId);
                ordersService.cancelOrderInternal(order);
            } else {
                log.info("订单ID: {} 状态已变为 {}，无需处理。消息将被确认。", orderId, order.getStatus());
            }

            // 手动确认消息，表示处理成功
            channel.basicAck(deliveryTag, false);
            log.info("超时订单ID: {} 处理完成，消息已确认。", orderId);

        } catch (Exception e) {
            log.error("处理超时订单ID: {} 失败，准备拒绝消息并让其重回队列或进入死信队列。", orderId, e);
            // 拒绝消息。第三个参数 re-queue 设置为 false，避免消息无限循环处理，
            // 如果RabbitMQ配置了死信队列，被拒绝的消息也会进入死信队列。
            channel.basicNack(deliveryTag, false, false);
        }
    }
}

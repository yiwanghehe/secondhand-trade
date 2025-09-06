package com.yw.secondhandtrade.server.mq.listener;

import com.rabbitmq.client.Channel;
import com.yw.secondhandtrade.common.constant.RabbitMQConstant;
import com.yw.secondhandtrade.pojo.dto.OrderMessageDTO;
import com.yw.secondhandtrade.server.websocket.WebSocketServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class NotificationMessageListener {

    /**
     * 监听订单创建成功通知队列
     * @param messageDTO 订单消息
     * @param message RabbitMQ消息对象
     * @param channel AMQP通道
     * @throws IOException
     */
    @RabbitListener(queues = RabbitMQConstant.ORDER_NOTIFY_QUEUE)
    public void handleOrderCreationNotification(OrderMessageDTO messageDTO, Message message, Channel channel) throws IOException {
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        log.info("收到订单创建通知: {}", messageDTO);

        try {
            // TODO 在这里可以实现邮件、短信通知等逻辑

            // 通过WebSocket向卖家推送实时通知
            String notificationContent = String.format("{\"type\":\"new_order\", \"message\":\"您有来自用户ID %d 的新订单，请及时处理！\", \"orderId\":%d}",
                    messageDTO.getBuyerId(), messageDTO.getOrderId());

            WebSocketServer.sendMessageToUser(messageDTO.getSellerId(), notificationContent);

            // 确认消息
            channel.basicAck(deliveryTag, false);

        } catch (Exception e) {
            log.error("处理订单创建通知失败，消息: {}", messageDTO, e);
            // 拒绝消息，不重新入队
            channel.basicNack(deliveryTag, false, false);
        }
    }
}

package com.yw.secondhandtrade.config;

import com.yw.secondhandtrade.common.constant.RabbitMQConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.Binding;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
@Slf4j
public class RabbitMQConfiguration {
    // 订单支付超时时间，单位：毫秒 (30分钟)
    private static final long ORDER_TTL = RabbitMQConstant.ORDER_TTL;

    /**
     * 配置消息转换器，使用Jackson2进行JSON序列化和反序列化
     * @return MessageConverter
     */
    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    // ======================= 订单超时自动取消的配置 (DLX + TTL) =======================

    /**
     * 声明订单业务交换机
     * @return a TopicExchange instance
     */
    @Bean
    public TopicExchange orderEventExchange() {
        return new TopicExchange(RabbitMQConstant.ORDER_EVENT_EXCHANGE, true, false);
    }

    /**
     * 声明订单延迟队列 (设置了TTL和死信交换机)
     * @return Queue instance
     */
    @Bean
    public Queue orderDelayQueue() {
        Map<String, Object> args = new HashMap<>();
        // 绑定死信交换机
        args.put("x-dead-letter-exchange", RabbitMQConstant.ORDER_DLX_EXCHANGE);
        // 绑定死信队列的路由键
        args.put("x-dead-letter-routing-key", RabbitMQConstant.ORDER_DEAD_ROUTING_KEY);
        // 设置消息存活时间
        args.put("x-message-ttl", ORDER_TTL);
        return new Queue(RabbitMQConstant.ORDER_DELAY_QUEUE, true, false, false, args);
    }

    /**
     * 绑定订单业务交换机和延迟队列
     * @return a Binding instance
     */
    @Bean
    public Binding orderDelayBinding() {
        return BindingBuilder.bind(orderDelayQueue()).to(orderEventExchange()).with(RabbitMQConstant.ORDER_CREATE_ROUTING_KEY);
    }

    /**
     * 声明死信交换机
     * @return a DirectExchange instance
     */
    @Bean
    public DirectExchange orderDlxExchange() {
        return new DirectExchange(RabbitMQConstant.ORDER_DLX_EXCHANGE, true, false);
    }

    /**
     * 声明死信队列
     * @return a Queue instance
     */
    @Bean
    public Queue orderDeadQueue() {
        return new Queue(RabbitMQConstant.ORDER_DEAD_QUEUE, true, false, false);
    }

    /**
     * 绑定死信交换机和死信队列
     * @return a Binding instance
     */
    @Bean
    public Binding orderDeadBinding() {
        return BindingBuilder.bind(orderDeadQueue()).to(orderDlxExchange()).with(RabbitMQConstant.ORDER_DEAD_ROUTING_KEY);
    }


    // ======================= 订单创建异步通知的配置 =======================

    /**
     * 声明订单通知主题交换机
     * @return a TopicExchange instance
     */
    @Bean
    public TopicExchange orderNotifyTopicExchange() {
        return new TopicExchange(RabbitMQConstant.ORDER_NOTIFY_TOPIC_EXCHANGE, true, false);
    }

    /**
     * 声明订单通知队列
     * @return a Queue instance
     */
    @Bean
    public Queue orderNotifyQueue() {
        return new Queue(RabbitMQConstant.ORDER_NOTIFY_QUEUE, true, false, false);
    }

    /**
     * 绑定订单通知交换机和队列
     * @return a Binding instance
     */
    @Bean
    public Binding orderNotifyBinding() {
        // 使用 "order.created.#" 路由键模式，可以匹配如 "order.created.notify", "order.created.log" 等
        return BindingBuilder.bind(orderNotifyQueue()).to(orderNotifyTopicExchange()).with("order.created.#");
    }


}

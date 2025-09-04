package com.yw.secondhandtrade.common.constant;

/**
 * RabbitMQ 常量类，用于统一定义交换机、队列和路由键的名称
 */
public class RabbitMQConstant {

    // ----------- 订单超时自动取消场景 -----------

    /**
     * 订单业务交换机 (Topic Exchange)
     * 用于接收所有与订单直接相关的业务消息，例如订单创建。
     */
    public static final String ORDER_EVENT_EXCHANGE = "order.event.exchange";

    /**
     * 订单延迟队列 (用于实现TTL)
     * 所有新创建的订单消息都先发送到这里，并设置了30分钟的TTL。
     * 30分钟后如果消息未被消费（即订单未支付），则会自动成为“死信”。
     */
    public static final String ORDER_DELAY_QUEUE = "order.delay.queue";

    /**
     * 订单延迟TTL
     */
    public static final Long ORDER_TTL = (long) (1000 * 60 * 30);

    /**
     * 订单延迟队列的路由键
     * 将订单创建消息路由到延迟队列。
     */
    public static final String ORDER_CREATE_ROUTING_KEY = "order.create";

    /**
     * 死信交换机 (Dead Letter Exchange)
     * 专门用于接收来自延迟队列的过期消息（死信）。
     */
    public static final String ORDER_DLX_EXCHANGE = "order.dlx.exchange";

    /**
     * 死信队列
     * 绑定到死信交换机，所有过期的订单消息最终都会进入这里，等待被消费者处理。
     */
    public static final String ORDER_DEAD_QUEUE = "order.dead.queue";

    /**
     * 死信队列的路由键
     * 将死信交换机收到的消息路由到死信队列。
     */
    public static final String ORDER_DEAD_ROUTING_KEY = "order.dead";


    // ----------- 订单创建后异步通知场景 -----------

    /**
     * 订单通知业务交换机 (Topic Exchange)
     * 用于发布订单相关的各种通知类消息，例如“订单已创建”、“订单已支付”等。
     * 使用Topic类型可以灵活地让不同消费者根据路由键订阅自己感兴趣的消息。
     */
    public static final String ORDER_NOTIFY_TOPIC_EXCHANGE = "order.notify.topic.exchange";

    /**
     * 订单创建成功通知队列
     * 专门用于处理订单创建成功后的异步通知任务，例如通过WebSocket通知卖家。
     */
    public static final String ORDER_NOTIFY_QUEUE = "order.notify.queue";

    /**
     * 订单创建成功通知的路由键
     * 发布者使用此路由键发送消息，订阅了`order.created.#`模式的消费者都能收到。
     */
    public static final String ORDER_NOTIFY_ROUTING_KEY = "order.created.notify";
}

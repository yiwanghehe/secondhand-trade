package com.yw.secondhandtrade.common.constant;

/**
 * 订单状态常量
 */
public class OrderStatusConstant {
    public static final Integer PENDING_PAYMENT = 0; // 待付款
    public static final Integer TO_BE_PICKED_UP = 1; // 待取货
    public static final Integer COMPLETED = 2;       // 已完成
    public static final Integer CANCELLED = 3;       // 已取消
    public static final Integer REFUNDED = 4;        // 已退款
}

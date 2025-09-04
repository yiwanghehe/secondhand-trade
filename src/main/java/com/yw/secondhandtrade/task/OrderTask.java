package com.yw.secondhandtrade.task;


import com.yw.secondhandtrade.common.constant.OrderStatusConstant;
import com.yw.secondhandtrade.mapper.OrdersMapper;
import com.yw.secondhandtrade.pojo.entity.Orders;
import com.yw.secondhandtrade.service.OrdersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 原有的定时任务类，现已被RabbitMQ的延迟队列+死信队列机制取代。
 */
//@Component
//@Slf4j
public class OrderTask {
    /*
    @Autowired
    OrdersMapper ordersMapper;

    @Autowired
    OrdersService ordersService;

    @Scheduled(cron = "0 * * * * ?") // 表示每分钟的第0秒触发
    @Transactional
    public void processTimeoutOrder(){
        log.info("开始处理超时订单");

        // 计算三十分钟前的时间点
        LocalDateTime timePoint = LocalDateTime.now().minusMinutes(30);

        // 查询超时未支付的订单
        List<Orders> timeoutOrders = ordersMapper.getByStatusAndCreateTimeLT(OrderStatusConstant.PENDING_PAYMENT, timePoint);

        if(timeoutOrders == null || timeoutOrders.isEmpty()){
            log.info("没有超时订单需要处理");
            return;
        }

        log.info("发现{}条超时订单需要处理", timeoutOrders.size());

        // 遍历并取消这些订单
        for (Orders order : timeoutOrders) {
            try {
                log.info("正在取消超时订单: {}", order.getId());
                ordersService.cancelOrderInternal(order);
            } catch (Exception e) {
                log.error("处理超时订单 {} 时发生错误: ", order.getId(), e);
            }
        }
        log.info("超时订单处理完成。");
    }
    */
}

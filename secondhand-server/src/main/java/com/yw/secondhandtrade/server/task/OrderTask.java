package com.yw.secondhandtrade.server.task;

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

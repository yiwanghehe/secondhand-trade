package com.yw.secondhandtrade.server.service;

import com.yw.secondhandtrade.pojo.dto.OrdersPageQueryDTO;
import com.yw.secondhandtrade.pojo.dto.OrdersSubmitDTO;
import com.yw.secondhandtrade.pojo.entity.Orders;
import com.yw.secondhandtrade.common.result.PageResult;
import com.yw.secondhandtrade.pojo.vo.OrderVO;
import com.yw.secondhandtrade.pojo.vo.OrdersSubmitVO;

public interface OrdersService {
    /**
     * 用户下单
     * @param ordersSubmitDTO
     * @return
     */
    OrdersSubmitVO submit(OrdersSubmitDTO ordersSubmitDTO);

    /**
     * 模拟支付
     * @param id
     */
    void pay(Long id);

    /**
     * 取消订单
     * @param id
     */
    void cancel(Long id);

    /**
     * [内部调用] 取消订单的核心逻辑，无权限校验
     * @param orders
     */
    void cancelOrderInternal(Orders orders);

    /**
     * 确认收货
     * @param id
     */
    void confirm(Long id);

    /**
     * 分页查询历史订单
     * @param ordersPageQueryDTO
     * @return
     */
    PageResult pageQuery(OrdersPageQueryDTO ordersPageQueryDTO);

    /**
     * 查询订单详情
     * @param id
     * @return
     */
    OrderVO details(Long id);
}

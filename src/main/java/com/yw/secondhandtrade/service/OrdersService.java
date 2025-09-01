package com.yw.secondhandtrade.service;

import com.yw.secondhandtrade.pojo.dto.OrdersSubmitDTO;
import com.yw.secondhandtrade.pojo.vo.OrdersSubmitVO;

public interface OrdersService {
    /**
     * 用户下单
     * @param ordersSubmitDTO
     * @return
     */
    OrdersSubmitVO submit(OrdersSubmitDTO ordersSubmitDTO);
}

package com.yw.secondhandtrade.mapper;

import com.yw.secondhandtrade.common.annotation.FillTime;
import com.yw.secondhandtrade.common.enumeration.DBOperationType;
import com.yw.secondhandtrade.pojo.entity.OrderDetails;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface OrderDetailsMapper {
    /**
     * 批量插入订单详情数据
     * @param orderDetailsList
     */
    @FillTime(DBOperationType.INSERT)
    void insertBatch(List<OrderDetails> orderDetailsList);

    /**
     * 根据订单ID查询订单详情
     * @param orderId
     * @return
     */
    List<OrderDetails> getByOrderId(Long orderId);
}

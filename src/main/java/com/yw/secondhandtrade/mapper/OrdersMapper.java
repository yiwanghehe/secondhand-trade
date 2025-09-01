package com.yw.secondhandtrade.mapper;

import com.yw.secondhandtrade.common.annotation.FillTime;
import com.yw.secondhandtrade.common.enumeration.DBOperationType;
import com.yw.secondhandtrade.pojo.entity.Orders;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrdersMapper {
    /**
     * 插入订单数据
     * @param orders
     */
    @FillTime(DBOperationType.INSERT)
    void insert(Orders orders);
}

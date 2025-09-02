package com.yw.secondhandtrade.mapper;

import com.github.pagehelper.Page;
import com.yw.secondhandtrade.common.annotation.FillTime;
import com.yw.secondhandtrade.common.enumeration.DBOperationType;
import com.yw.secondhandtrade.pojo.dto.OrdersPageQueryDTO;
import com.yw.secondhandtrade.pojo.entity.Orders;
import com.yw.secondhandtrade.pojo.vo.OrderVO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrdersMapper {
    /**
     * 插入订单数据
     * @param orders
     */
    @FillTime(DBOperationType.INSERT)
    void insert(Orders orders);

    /**
     * 根据ID查询订单
     * @param id
     * @return
     */
    Orders getById(Long id);

    /**
     * 更新订单信息
     * @param orders
     */
    @FillTime(DBOperationType.UPDATE)
    void update(Orders orders);

    /**
     * 分页查询订单
     * @param ordersPageQueryDTO
     * @return
     */
    Page<OrderVO> pageQuery(OrdersPageQueryDTO ordersPageQueryDTO);
}

package com.yw.secondhandtrade.server.mapper;

import com.github.pagehelper.Page;
import com.yw.secondhandtrade.pojo.dto.OrdersPageQueryDTO;
import com.yw.secondhandtrade.pojo.entity.Orders;
import com.yw.secondhandtrade.common.enumeration.DBOperationType;
import com.yw.secondhandtrade.pojo.vo.OrderVO;
import com.yw.secondhandtrade.server.annotation.FillTime;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.time.LocalDateTime;
import java.util.List;

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

    /**
     * 根据状态和创建时间查询订单
     * @param status 订单状态
     * @param createTime 创建时间阈值
     * @return
     */
    List<Orders> getByStatusAndCreateTimeLT(@Param("status") Integer status, @Param("createTime") LocalDateTime createTime);
}

package com.yw.secondhandtrade.server.mapper;

import com.yw.secondhandtrade.pojo.entity.Rating;
import com.yw.secondhandtrade.common.enumeration.DBOperationType;
import com.yw.secondhandtrade.server.annotation.FillTime;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface RatingMapper {
    /**
     * 插入评价数据
     * @param rating
     */
    @FillTime(DBOperationType.INSERT)
    void insert(Rating rating);

    /**
     * 根据订单ID和评价人ID查询评价
     * @param orderId
     * @param raterId
     * @return
     */
    Rating getByOrderIdAndRaterId(@Param("orderId") Long orderId, @Param("raterId") Long raterId);
}

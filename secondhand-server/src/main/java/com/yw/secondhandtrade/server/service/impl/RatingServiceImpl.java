package com.yw.secondhandtrade.server.service.impl;

import com.yw.secondhandtrade.common.constant.MessageConstant;
import com.yw.secondhandtrade.common.constant.OrderStatusConstant;
import com.yw.secondhandtrade.common.context.BaseContext;
import com.yw.secondhandtrade.pojo.dto.RatingDTO;
import com.yw.secondhandtrade.pojo.entity.Orders;
import com.yw.secondhandtrade.pojo.entity.Rating;
import com.yw.secondhandtrade.common.exception.BusinessException;
import com.yw.secondhandtrade.server.mapper.OrdersMapper;
import com.yw.secondhandtrade.server.mapper.RatingMapper;
import com.yw.secondhandtrade.server.service.RatingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RatingServiceImpl implements RatingService {

    @Autowired
    private RatingMapper ratingMapper;
    @Autowired
    private OrdersMapper ordersMapper;

    @Override
    @Transactional
    public void submit(RatingDTO ratingDTO) {
        Long currentUserId = BaseContext.getId();

        // 校验订单是否存在，且当前用户是买家或卖家
        Orders order = ordersMapper.getById(ratingDTO.getOrderId());
        if (order == null || (!order.getBuyerId().equals(currentUserId) && !order.getSellerId().equals(currentUserId))) {
            throw new BusinessException(MessageConstant.ORDER_NOT_FOUND_OR_NO_PERMISSION);
        }

        // 校验订单状态是否为“已完成”
        if (!order.getStatus().equals(OrderStatusConstant.COMPLETED)) {
            throw new BusinessException(MessageConstant.ORDER_NOT_COMPLETED);
        }

        // 校验是否已评价过
        Rating existingRating = ratingMapper.getByOrderIdAndRaterId(ratingDTO.getOrderId(), currentUserId);
        if (existingRating != null) {
            throw new BusinessException(MessageConstant.ORDER_ALREADY_RATED);
        }

        // 确定被评价人ID
        Long rateeId;
        if (order.getBuyerId().equals(currentUserId)) {
            // 如果当前用户是买家，则被评价人是卖家
            rateeId = order.getSellerId();
        } else {
            // 如果当前用户是卖家，则被评价人是买家
            rateeId = order.getBuyerId();
        }

        // 构造评价数据并插入数据库
        Rating rating = Rating.builder()
                .orderId(ratingDTO.getOrderId())
                .raterId(currentUserId)
                .rateeId(rateeId)
                .score(ratingDTO.getScore())
                .comment(ratingDTO.getComment())
                .build();

        ratingMapper.insert(rating);
    }
}

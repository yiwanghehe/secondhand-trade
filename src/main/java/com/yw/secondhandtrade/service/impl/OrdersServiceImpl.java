package com.yw.secondhandtrade.service.impl;

import com.yw.secondhandtrade.common.constant.MessageConstant;
import com.yw.secondhandtrade.common.constant.OrderStatus;
import com.yw.secondhandtrade.common.context.BaseContext;
import com.yw.secondhandtrade.common.exception.BusinessException;
import com.yw.secondhandtrade.mapper.AddressMapper;
import com.yw.secondhandtrade.mapper.GoodsMapper;
import com.yw.secondhandtrade.mapper.OrderDetailsMapper;
import com.yw.secondhandtrade.mapper.OrdersMapper;
import com.yw.secondhandtrade.pojo.dto.OrderItemDTO;
import com.yw.secondhandtrade.pojo.dto.OrdersSubmitDTO;
import com.yw.secondhandtrade.pojo.entity.Address;
import com.yw.secondhandtrade.pojo.entity.Goods;
import com.yw.secondhandtrade.pojo.entity.OrderDetails;
import com.yw.secondhandtrade.pojo.entity.Orders;
import com.yw.secondhandtrade.pojo.vo.OrdersSubmitVO;
import com.yw.secondhandtrade.service.OrdersService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class OrdersServiceImpl implements OrdersService {

    @Autowired
    private OrdersMapper ordersMapper;
    @Autowired
    private OrderDetailsMapper orderDetailsMapper;
    @Autowired
    private GoodsMapper goodsMapper;
    @Autowired
    private AddressMapper addressMapper;

    @Override
    @Transactional
    public OrdersSubmitVO submit(OrdersSubmitDTO ordersSubmitDTO) {
        // 处理业务异常（地址为空，商品列表为空）
        Address address = addressMapper.getById(ordersSubmitDTO.getAddressId());
        if (address == null || !address.getUserId().equals(BaseContext.getId())) {
            throw new BusinessException(MessageConstant.ADDRESS_NOT_FOUND_OR_NO_PERMISSION);
        }
        if (ordersSubmitDTO.getItems() == null || ordersSubmitDTO.getItems().isEmpty()) {
            throw new BusinessException(MessageConstant.ORDER_ITEMS_EMPTY);
        }

        // 准备订单数据
        Long currentUserId = BaseContext.getId();
        BigDecimal totalAmount = BigDecimal.ZERO;
        Long sellerId = null;
        List<OrderDetails> orderDetailsList = new ArrayList<>();

        // 遍历商品列表，校验并计算总价
        for (OrderItemDTO item : ordersSubmitDTO.getItems()) {
            Goods goods = goodsMapper.getById(item.getGoodsId());

            if (goods == null) {
                throw new BusinessException(MessageConstant.GOODS_NOT_FOUND_OR_NO_PERMISSION);
            }
            if (sellerId == null) {
                sellerId = goods.getSellerId();
            } else if (!sellerId.equals(goods.getSellerId())) {
                throw new BusinessException(MessageConstant.ORDER_CANNOT_MIX_SELLERS);
            }
            if (goods.getStock() < item.getQuantity()) {
                throw new BusinessException(goods.getName() + MessageConstant.STOCK_NOT_ENOUGH);
            }

            totalAmount = totalAmount.add(goods.getPrice().multiply(new BigDecimal(item.getQuantity())));

            OrderDetails orderDetails = OrderDetails.builder()
                    .goodsId(goods.getId())
                    .goodsName(goods.getName())
                    .goodsImage(goods.getImages())
                    .price(goods.getPrice())
                    .quantity(item.getQuantity())
                    .build();
            orderDetailsList.add(orderDetails);

            // 扣减库存
            goods.setStock(goods.getStock() - item.getQuantity());
            goodsMapper.updateStock(goods);
        }

        // 构建订单并插入数据库
        Orders order = Orders.builder()
                .orderNo(UUID.randomUUID().toString().replace("-", ""))
                .buyerId(currentUserId)
                .sellerId(sellerId)
                .totalAmount(totalAmount)
                .tradeLocation(address.getDetailLocation())
                .consignee(address.getConsignee())
                .contactPhone(address.getPhone())
                .status(OrderStatus.PENDING_PAYMENT) // 初始状态为待支付
                .createTime(LocalDateTime.now())
                .build();
        ordersMapper.insert(order);

        // 批量插入订单详情
        for (OrderDetails detail : orderDetailsList) {
            detail.setOrderId(order.getId());
        }
        orderDetailsMapper.insertBatch(orderDetailsList);

        // 封装返回结果
        return OrdersSubmitVO.builder()
                .id(order.getId())
                .orderNo(order.getOrderNo())
                .orderAmount(order.getTotalAmount())
                .orderTime(order.getCreateTime())
                .build();
    }
}

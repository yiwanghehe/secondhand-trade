package com.yw.secondhandtrade.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.yw.secondhandtrade.common.constant.MessageConstant;
import com.yw.secondhandtrade.common.constant.OrderStatusConstant;
import com.yw.secondhandtrade.common.context.BaseContext;
import com.yw.secondhandtrade.common.exception.BusinessException;
import com.yw.secondhandtrade.common.result.PageResult;
import com.yw.secondhandtrade.mapper.AddressMapper;
import com.yw.secondhandtrade.mapper.GoodsMapper;
import com.yw.secondhandtrade.mapper.OrderDetailsMapper;
import com.yw.secondhandtrade.mapper.OrdersMapper;
import com.yw.secondhandtrade.pojo.dto.OrderItemDTO;
import com.yw.secondhandtrade.pojo.dto.OrdersPageQueryDTO;
import com.yw.secondhandtrade.pojo.dto.OrdersSubmitDTO;
import com.yw.secondhandtrade.pojo.entity.Address;
import com.yw.secondhandtrade.pojo.entity.Goods;
import com.yw.secondhandtrade.pojo.entity.OrderDetails;
import com.yw.secondhandtrade.pojo.entity.Orders;
import com.yw.secondhandtrade.pojo.vo.OrderVO;
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
                .status(OrderStatusConstant.PENDING_PAYMENT) // 初始状态为待支付
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

    @Override
    public void pay(Long orderId){
        Long currentUserId = BaseContext.getId();
        // 校验订单
        Orders orders = checkOrderPermission(orderId, BaseContext.getId());

        // 只有待付款订单才能支付
        if (!orders.getStatus().equals(OrderStatusConstant.PENDING_PAYMENT)) {
            throw new BusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }

        // TODO 需要补充实际的支付逻辑

        Orders ordersToUpdate = Orders.builder()
                .id(orderId)
                .status(OrderStatusConstant.TO_BE_PICKED_UP)
                .paymentTime(LocalDateTime.now())
                .build();

        ordersMapper.update(ordersToUpdate);
    }

    @Override
    @Transactional
    public void cancel(Long orderId) {
        Orders orders = checkOrderPermission(orderId, BaseContext.getId());

        // 只有待付款订单才能取消
        if (!orders.getStatus().equals(OrderStatusConstant.PENDING_PAYMENT)) {
            throw new BusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }

        // 归还商品库存
        List<OrderDetails> orderDetails = orderDetailsMapper.getByOrderId(orderId);
        for (OrderDetails detail : orderDetails) {
            Goods goods = goodsMapper.getById(detail.getGoodsId());
            goods.setStock(goods.getStock() + detail.getQuantity());
            goodsMapper.updateStock(goods);
        }

        Orders ordersToUpdate = Orders.builder()
                .id(orderId)
                .status(OrderStatusConstant.CANCELLED)
                .completionTime(LocalDateTime.now()) // 取消时间
                .build();

        ordersMapper.update(ordersToUpdate);
    }

    @Override
    public void confirm(Long orderId) {
        Orders orders = checkOrderPermission(orderId, BaseContext.getId());

        // 只有待取货订单才能确认
        if (!orders.getStatus().equals(OrderStatusConstant.TO_BE_PICKED_UP)) {
            throw new BusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }

        Orders ordersToUpdate = Orders.builder()
                .id(orderId)
                .status(OrderStatusConstant.COMPLETED)
                .completionTime(LocalDateTime.now())
                .build();

        ordersMapper.update(ordersToUpdate);
    }

    @Override
    public PageResult pageQuery(OrdersPageQueryDTO ordersPageQueryDTO) {
        PageHelper.startPage(ordersPageQueryDTO.getPage(), ordersPageQueryDTO.getPageSize());

        ordersPageQueryDTO.setUserId(BaseContext.getId());
        Page<OrderVO> page = ordersMapper.pageQuery(ordersPageQueryDTO);

        // 为每个订单查询订单详情
        if (page.getResult() != null && !page.getResult().isEmpty()) {
            for (OrderVO orderVO : page.getResult()) {
                List<OrderDetails> orderDetails = orderDetailsMapper.getByOrderId(orderVO.getId());
                orderVO.setOrderDetails(orderDetails);
            }
        }
        return new PageResult(page.getTotal(), page.getResult());
    }

    @Override
    public OrderVO details(Long orderId) {
        Orders orders = checkOrderPermission(orderId, BaseContext.getId());

        OrderVO orderVO = new OrderVO();
        BeanUtils.copyProperties(orders, orderVO);
        orderVO.setOrderDetails(orderDetailsMapper.getByOrderId(orderId));
        return orderVO;
    }




    /**
     * 校验订单是否存在以及是否属于当前用户
     * @param orderId 订单ID
     * @param currentUserId 当前用户ID
     * @return 订单实体
     */
    private Orders checkOrderPermission(Long orderId, Long currentUserId){
        Orders orders = ordersMapper.getById(orderId);
        if(orders == null){
            throw new BusinessException(MessageConstant.ORDER_NOT_FOUND_OR_NO_PERMISSION);
        }

        if(!orders.getBuyerId().equals(currentUserId) && !orders.getSellerId().equals(currentUserId)){
            throw new BusinessException(MessageConstant.ORDER_NOT_FOUND_OR_NO_PERMISSION);
        }

        return orders;
    }

}

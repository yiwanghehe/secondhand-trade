package com.yw.secondhandtrade.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Orders implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private String orderNo;
    private Long goodsId;
    private Long buyerId;
    private Long sellerId;
    private BigDecimal totalAmount;

    // 交易快照字段
    private String tradeLocation;
    private String consignee;
    private String contactPhone;


    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime paymentTime;
    private LocalDateTime completionTime;
    private LocalDateTime updateTime;
}

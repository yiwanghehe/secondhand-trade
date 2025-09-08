package com.yw.secondhandtrade.pojo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "用户下单成功返回的数据格式")
public class OrdersSubmitVO implements Serializable {

    @Schema(description = "订单ID")
    private Long id;

    @Schema(description = "订单号")
    private String orderNo;

    @Schema(description = "订单总金额")
    private BigDecimal orderAmount;

    @Schema(description = "下单时间")
    private LocalDateTime orderTime;
}

package com.yw.secondhandtrade.pojo.vo;

import com.yw.secondhandtrade.pojo.entity.OrderDetails;
import com.yw.secondhandtrade.pojo.entity.Orders;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "订单视图对象")
public class OrderVO extends Orders implements Serializable {
    @Schema(description = "订单详情")
    private List<OrderDetails> orderDetails;
}

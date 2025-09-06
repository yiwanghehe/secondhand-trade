package com.yw.secondhandtrade.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;
import java.util.List;

@Data
@Schema(description = "用户下单数据传输对象")
public class OrdersSubmitDTO implements Serializable {

    @Schema(description = "地址簿ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long addressId;

    @Schema(description = "购买的商品列表", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<OrderItemDTO> items;
}

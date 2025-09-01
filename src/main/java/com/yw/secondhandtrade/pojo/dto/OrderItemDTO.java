package com.yw.secondhandtrade.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;

@Data
@Schema(description = "订单中的商品项")
public class OrderItemDTO implements Serializable {

    @Schema(description = "商品ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long goodsId;

    @Schema(description = "购买数量", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer quantity;
}

package com.yw.secondhandtrade.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = "订单分页查询数据传输对象")
public class OrdersPageQueryDTO implements Serializable {

    @Schema(description = "用户ID", hidden = true)
    private Long userId;

    @Schema(description = "页码")
    private int page;

    @Schema(description = "每页记录数")
    private int pageSize;

    @Schema(description = "订单状态")
    private Integer status;
}

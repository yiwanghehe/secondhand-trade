package com.yw.secondhandtrade.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;

@Data
@Schema(description = "评价数据传输对象")
public class RatingDTO implements Serializable {

    @Schema(description = "订单ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long orderId;

    @Schema(description = "评分 (1-5)", requiredMode = Schema.RequiredMode.REQUIRED, example = "5")
    private Integer score;

    @Schema(description = "评论内容", example = "交易很愉快！")
    private String comment;
}

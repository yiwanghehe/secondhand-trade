package com.yw.secondhandtrade.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Schema(description = "商品数据传输对象")
public class GoodsDTO implements Serializable {

    @Schema(description = "商品ID (修改时需要提供)", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Schema(description = "分类ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long categoryId;

    @Schema(description = "商品名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "二手编译原理教科书")
    private String name;

    @Schema(description = "商品描述", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "女大自用二手")
    private String description;

    @Schema(description = "价格", requiredMode = Schema.RequiredMode.REQUIRED, example = "5")
    private BigDecimal price;

    @Schema(description = "图片", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "https://your-bucket-name.oss-cn-chengdu.aliyuncs.com/avatar.jpg")
    private String images;

    @Schema(description = "库存", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer stock;
}

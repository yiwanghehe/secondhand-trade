package com.yw.secondhandtrade.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;

@Data
@Schema(description = "收藏夹数据传输对象")
public class FavoritesDTO implements Serializable {

    @Schema(description = "商品ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long goodsId;
}

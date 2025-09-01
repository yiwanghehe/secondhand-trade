package com.yw.secondhandtrade.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;

@Data
@Schema(description = "【用户端】商品分页查询数据传输对象")
public class GoodsPageQueryDTO implements Serializable {

    @Schema(description = "页码", example = "1")
    private int page;

    @Schema(description = "每页记录数", example = "10")
    private int pageSize;

    @Schema(description = "商品名称关键字 (用于模糊搜索)", example = "书")
    private String name;

    @Schema(description = "商品分类ID", example = "1")
    private Long categoryId;
}

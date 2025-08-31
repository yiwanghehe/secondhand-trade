package com.yw.secondhandtrade.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;

@Data
@Schema(description = "商品分类数据传输对象")
public class CategoryDTO implements Serializable {

    @Schema(description = "分类ID (修改时需要)", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Schema(description = "分类名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "书籍教材")
    private String name;

    @Schema(description = "排序权重，数字越小越靠前", requiredMode = Schema.RequiredMode.REQUIRED, example = "0")
    private Integer sortOrder;
}

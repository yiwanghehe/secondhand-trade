package com.yw.secondhandtrade.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;

@Data
@Schema(description = "商品分类分页查询数据传输对象")
public class CategoryPageQueryDTO implements Serializable {

    @Schema(description = "页码", example = "1")
    private int page;

    @Schema(description = "每页记录数", example = "10")
    private int pageSize;

    @Schema(description = "分类名称 (用于模糊搜索)", example = "书籍")
    private String name;
}

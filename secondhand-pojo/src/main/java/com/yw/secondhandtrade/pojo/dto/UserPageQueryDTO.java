package com.yw.secondhandtrade.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = "用户分页查询数据传输对象")
public class UserPageQueryDTO implements Serializable {
    @Schema(description = "姓名或昵称，模糊查询")
    private String name;

    @Schema(description = "页码")
    private int page;

    @Schema(description = "每页记录数")
    private int pageSize;
}


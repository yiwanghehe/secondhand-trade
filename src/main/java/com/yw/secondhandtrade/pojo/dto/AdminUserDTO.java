package com.yw.secondhandtrade.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = "【管理端专用】用户数据传输对象")
public class AdminUserDTO implements Serializable {

    @Schema(description = "用户ID (修改时必须提供)")
    private Long id;

    @Schema(description = "登录用户名", requiredMode = Schema.RequiredMode.REQUIRED, example = "newadmin")
    private String username;

    @Schema(description = "登录密码", example = "123456")
    private String password;

    @Schema(description = "姓名", example = "李四")
    private String name;

    @Schema(description = "昵称", example = "小李飞刀")
    private String nickname;

    @Schema(description = "手机号码", example = "13900139000")
    private String phone;

    @Schema(description = "角色: 1-普通用户, 2-管理员", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    private Integer role;
}

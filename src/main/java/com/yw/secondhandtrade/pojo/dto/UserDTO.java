package com.yw.secondhandtrade.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = "用户数据传输对象")
public class UserDTO implements Serializable {

    @Schema(description = "用户ID (修改时使用)", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Schema(description = "登录用户名", requiredMode = Schema.RequiredMode.REQUIRED, example = "testuser")
    private String username;

    @Schema(description = "登录密码", requiredMode = Schema.RequiredMode.REQUIRED, example = "123456")
    private String password;

    @Schema(description = "姓名 (管理员使用)", example = "张三")
    private String name;

    @Schema(description = "昵称 (普通用户使用)", example = "测试用户")
    private String nickname;

    @Schema(description = "手机号码", example = "13800138000")
    private String phone;
}


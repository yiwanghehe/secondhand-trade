package com.yw.secondhandtrade.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = "用户数据传输对象")
public class UserDTO implements Serializable {

    @Schema(description = "用户ID (系统生成，无需填写)", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Schema(description = "登录用户名", requiredMode = Schema.RequiredMode.REQUIRED, example = "yiwanghehe")
    private String username;

    @Schema(description = "登录密码", requiredMode = Schema.RequiredMode.REQUIRED, example = "123456")
    private String password;

    @Schema(description = "昵称", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "yiwanghehe123")
    private String nickname;

    @Schema(description = "头像URL", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "asdasdasd")
    private String avatarUrl;

    @Schema(description = "手机号码", requiredMode = Schema.RequiredMode.REQUIRED, example = "13800138000")
    private String phone;

}

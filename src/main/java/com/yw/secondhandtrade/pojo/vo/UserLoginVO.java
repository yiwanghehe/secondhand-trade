package com.yw.secondhandtrade.pojo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "用户登录返回的数据格式")
public class UserLoginVO implements Serializable {

    @Schema(description = "主键值")
    private Long id;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "姓名 (管理员)")
    private String name;

    @Schema(description = "昵称 (普通用户)")
    private String nickname;

    @Schema(description = "角色 1:普通用户 2:管理员")
    private Integer role;

    @Schema(description = "jwt令牌")
    private String token;
}


package com.yw.secondhandtrade.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;

@Data
@Schema(description = "员工数据传输对象")
public class EmployeeDTO implements Serializable {

    @Schema(description = "员工ID (系统生成，无需填写)", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Schema(description = "登录用户名", requiredMode = Schema.RequiredMode.REQUIRED, example = "zhangsan")
    private String username;

    @Schema(description = "登录密码", requiredMode = Schema.RequiredMode.REQUIRED, example = "123456")
    private String password;

    @Schema(description = "真实姓名", requiredMode = Schema.RequiredMode.REQUIRED, example = "张三")
    private String name;

    @Schema(description = "手机号码", requiredMode = Schema.RequiredMode.REQUIRED, example = "13800138000")
    private String phone;
}

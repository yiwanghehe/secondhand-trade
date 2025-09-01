package com.yw.secondhandtrade.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;

@Data
@Schema(description = "地址数据传输对象")
public class AddressDTO implements Serializable {

    @Schema(description = "主键ID (修改时需要)")
    private Long id;

    @Schema(description = "联系人姓名", requiredMode = Schema.RequiredMode.REQUIRED, example = "王同学")
    private String consignee;

    @Schema(description = "手机号", requiredMode = Schema.RequiredMode.REQUIRED, example = "13800138000")
    private String phone;

    @Schema(description = "常用交易地点", requiredMode = Schema.RequiredMode.REQUIRED, example = "一食堂门口")
    private String detail_location;

    @Schema(description = "是否为默认地址 (0:否, 1:是)", example = "0")
    private Integer isDefault;
}

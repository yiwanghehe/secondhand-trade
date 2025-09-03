package com.yw.secondhandtrade.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = "聊天消息数据传输对象")
public class ChatMessageDTO implements Serializable {

    @Schema(description = "接收者ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    private Long toUserId;

    @Schema(description = "消息内容", requiredMode = Schema.RequiredMode.REQUIRED, example = "你好，这个商品还在吗？")
    private String content;

}

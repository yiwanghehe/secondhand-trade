package com.yw.secondhandtrade.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long fromUserId;
    private Long toUserId;
    private String content;
    private LocalDateTime sendTime;
    private Integer readStatus;
}

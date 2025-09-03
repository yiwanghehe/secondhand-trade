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
public class Rating implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long orderId;
    private Long raterId;
    private Long rateeId;
    private Integer score;
    private String comment;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}

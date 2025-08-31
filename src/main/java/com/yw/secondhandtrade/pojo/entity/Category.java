package com.yw.secondhandtrade.pojo.entity;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

public class Category implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private Integer sort_order;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}

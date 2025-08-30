package com.yw.secondhandtrade.pojo.entity;

import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Goods implements Serializable {
    private Long id;
    private Long sellerId;
    private Long categoryId;
    private String name;
    private String description;
    private Double price;
    private String images;
    private Integer status;
    private Integer stock;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

}

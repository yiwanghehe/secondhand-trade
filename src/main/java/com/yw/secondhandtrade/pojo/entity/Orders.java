package com.yw.secondhandtrade.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Orders implements Serializable {
    private Long id;
    private String orderNo;
    private Long goodsId;
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

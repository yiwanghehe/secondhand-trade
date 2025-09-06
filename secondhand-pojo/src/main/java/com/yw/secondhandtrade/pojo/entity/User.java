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
public class User implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private String username;
    private String password;
    private String name; // 管理员姓名
    private String nickname; // 用户昵称
    private String avatarUrl;
    private String phone;
    private Integer role; // 1:普通用户 2:管理员
    private Integer status;
    private String authType;
    private String githubUsername; // 用于存储GitHub用户的唯一标识 (用户名)
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}


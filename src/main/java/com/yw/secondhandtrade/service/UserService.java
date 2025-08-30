package com.yw.secondhandtrade.service;

import com.yw.secondhandtrade.pojo.dto.UserDTO;
import com.yw.secondhandtrade.pojo.dto.UserLoginDTO;
import com.yw.secondhandtrade.pojo.entity.User;

public interface UserService {

    /**
     * 注册用户
     * @param userDTO
     */
    void save(UserDTO userDTO);

    /**
     * 用户登录
     * @param userLoginDTO
     * @return
     */
    User login(UserLoginDTO userLoginDTO);


}

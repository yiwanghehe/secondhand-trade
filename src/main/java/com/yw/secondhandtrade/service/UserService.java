package com.yw.secondhandtrade.service;

import com.yw.secondhandtrade.common.result.PageResult;
import com.yw.secondhandtrade.pojo.dto.AdminUserDTO;
import com.yw.secondhandtrade.pojo.dto.UserDTO;
import com.yw.secondhandtrade.pojo.dto.UserLoginDTO;
import com.yw.secondhandtrade.pojo.dto.UserPageQueryDTO;
import com.yw.secondhandtrade.pojo.entity.User;

public interface UserService {

    /**
     * 通用登录接口
     * @param userLoginDTO
     * @return
     */
    User login(UserLoginDTO userLoginDTO);

    /**
     * 【用户端】注册普通用户
     * @param userDTO
     */
    void register(UserDTO userDTO);

    /**
     * 【用户端】编辑当前用户信息
     * @param userDTO
     */
    void updateCurrent(UserDTO userDTO);

    // --- 以下为管理端方法 ---

    /**
     * 【管理端】新增用户（可指定角色）
     * @param adminUserDTO
     */
    void save(AdminUserDTO adminUserDTO);

    /**
     * 【管理端】用户分页查询
     * @param userPageQueryDTO
     * @return
     */
    PageResult pageQuery(UserPageQueryDTO userPageQueryDTO);

    /**
     * 【管理端】启用或禁用用户
     * @param status
     * @param id
     */
    void startOrStop(Integer status, Long id);

    /**
     * 【通用】根据ID查询用户
     * @param id
     * @return
     */
    User getById(Long id);

    /**
     * 【管理端】编辑用户信息
     * @param adminUserDTO
     */
    void update(AdminUserDTO adminUserDTO);
}


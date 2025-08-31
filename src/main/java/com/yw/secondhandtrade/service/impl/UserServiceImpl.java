package com.yw.secondhandtrade.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.yw.secondhandtrade.common.constant.MessageConstant;
import com.yw.secondhandtrade.common.constant.RoleConstant;
import com.yw.secondhandtrade.common.constant.StatusConstant;
import com.yw.secondhandtrade.common.context.BaseContext;
import com.yw.secondhandtrade.common.exception.AccountLockedException;
import com.yw.secondhandtrade.common.exception.AccountNotFoundException;
import com.yw.secondhandtrade.common.exception.PasswordEmptyException;
import com.yw.secondhandtrade.common.exception.PasswordErrorException;
import com.yw.secondhandtrade.common.result.PageResult;
import com.yw.secondhandtrade.mapper.UserMapper;
import com.yw.secondhandtrade.pojo.dto.AdminUserDTO;
import com.yw.secondhandtrade.pojo.dto.UserDTO;
import com.yw.secondhandtrade.pojo.dto.UserLoginDTO;
import com.yw.secondhandtrade.pojo.dto.UserPageQueryDTO;
import com.yw.secondhandtrade.pojo.entity.User;
import com.yw.secondhandtrade.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.util.Objects;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    /**
     * 通用登录
     */
    @Override
    public User login(UserLoginDTO userLoginDTO) {
        String username = userLoginDTO.getUsername();
        String password = userLoginDTO.getPassword();

        User user = userMapper.getByUsername(username);

        if (user == null) {
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        password = DigestUtils.md5DigestAsHex(password.getBytes());
        if (!password.equals(user.getPassword())) {
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }

        if (Objects.equals(user.getStatus(), StatusConstant.DISABLE)) {
            throw new AccountLockedException(MessageConstant.ACCOUNT_LOCKED);
        }

        return user;
    }

    /**
     * 注册普通用户
     */
    @Override
    @Transactional
    public void register(UserDTO userDTO) {
        User user = new User();
        BeanUtils.copyProperties(userDTO, user);

        user.setRole(RoleConstant.USER); // 默认角色为普通用户
        user.setStatus(StatusConstant.ENABLE);
        user.setPassword(DigestUtils.md5DigestAsHex(userDTO.getPassword().getBytes()));

        userMapper.insert(user);
    }

    /**
     * 【用户端】编辑当前用户信息
     * @param userDTO
     */
    @Override
    public void updateCurrent(UserDTO userDTO) {
        // 获取当前登录用户的ID，确保用户只能修改自己的信息
        Long currentUserId = BaseContext.getId();

        // 将ID设置到要更新的对象中
        User user = new User();
        BeanUtils.copyProperties(userDTO, user);
        user.setId(currentUserId);

        // 防止用户通过此接口修改角色或状态
        user.setRole(RoleConstant.USER);
        user.setStatus(StatusConstant.ENABLE);

        // 加密
        if(userDTO.getPassword() != null && !userDTO.getPassword().isEmpty()){
            user.setPassword(DigestUtils.md5DigestAsHex(userDTO.getPassword().getBytes()));
        } else {
            throw new PasswordEmptyException(MessageConstant.PASSWORD_EMPTY);
        }

        userMapper.update(user);
    }

    // --- 管理端方法实现 ---

    /**
     * 【管理端】新增用户
     */
    @Override
    @Transactional
    public void save(AdminUserDTO adminUserDTO) {
        User user = new User();
        BeanUtils.copyProperties(adminUserDTO, user);

        // 如果不指定角色，默认为普通用户
        if(user.getRole() == null){
            user.setRole(RoleConstant.USER);
        }
        user.setStatus(StatusConstant.ENABLE);
        user.setPassword(DigestUtils.md5DigestAsHex(adminUserDTO.getPassword().getBytes()));

        userMapper.insert(user);
    }

    /**
     * 【管理端】用户分页查询
     */
    @Override
    public PageResult pageQuery(UserPageQueryDTO userPageQueryDTO) {
        PageHelper.startPage(userPageQueryDTO.getPage(), userPageQueryDTO.getPageSize());
        Page<User> page = userMapper.pageQuery(userPageQueryDTO);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 【管理端】启用禁用账户
     */
    @Override
    public void startOrStop(Integer status, Long id) {
        User user = User.builder()
                .status(status)
                .id(id)
                .build();
        userMapper.update(user);
    }

    /**
     * 【通用】根据ID查询
     */
    @Override
    public User getById(Long id) {
        User user = userMapper.getById(id);
        if (user != null) {
            user.setPassword("****"); // 保护密码不泄露
        } else {
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }
        return user;
    }

    /**
     * 【管理端】编辑用户信息
     */
    @Override
    public void update(AdminUserDTO adminUserDTO) {
        User user = new User();
        BeanUtils.copyProperties(adminUserDTO, user);

        // 如果密码不为空，则进行加密
        if(adminUserDTO.getPassword() != null && !adminUserDTO.getPassword().isEmpty()){
            user.setPassword(DigestUtils.md5DigestAsHex(adminUserDTO.getPassword().getBytes()));
        }

        userMapper.update(user);
    }
}


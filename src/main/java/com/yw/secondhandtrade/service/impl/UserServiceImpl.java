package com.yw.secondhandtrade.service.impl;

import com.yw.secondhandtrade.common.constant.MessageConstant;
import com.yw.secondhandtrade.common.constant.StatusConstant;
import com.yw.secondhandtrade.common.exception.AccountLockedException;
import com.yw.secondhandtrade.common.exception.AccountNotFoundException;
import com.yw.secondhandtrade.common.exception.PasswordErrorException;
import com.yw.secondhandtrade.mapper.UserMapper;
import com.yw.secondhandtrade.pojo.dto.UserDTO;
import com.yw.secondhandtrade.pojo.dto.UserLoginDTO;
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
    UserMapper userMapper;

    /**
     * 注册用户
     * @param userDTO
     */
    @Override
    @Transactional
    public void save(UserDTO userDTO) {
        User user = new User();
        BeanUtils.copyProperties(userDTO, user);

        user.setStatus(StatusConstant.ENABLE);
        user.setPassword(DigestUtils.md5DigestAsHex(userDTO.getPassword().getBytes()));

        userMapper.insert(user);
    }

    /**
     * 用户登录
     * @param userLoginDTO
     * @return
     */
    @Override
    public User login(UserLoginDTO userLoginDTO) {
        String username = userLoginDTO.getUsername();
        String password = userLoginDTO.getPassword();

        //1、根据用户名查询数据库中的数据
        User user = userMapper.getByUsername(username);

        //2、处理各种异常情况（用户名不存在、密码不对、账号被锁定）
        if (user == null) {
            //账号不存在
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        //密码比对
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        if (!password.equals(user.getPassword())) {
            //密码错误
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }

        if (Objects.equals(user.getStatus(), StatusConstant.DISABLE)) {
            //账号被锁定
            throw new AccountLockedException(MessageConstant.ACCOUNT_LOCKED);
        }

        //3、返回实体对象
        return user;
    }
}

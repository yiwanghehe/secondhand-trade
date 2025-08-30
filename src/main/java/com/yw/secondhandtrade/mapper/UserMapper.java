package com.yw.secondhandtrade.mapper;


import com.yw.secondhandtrade.common.annotation.FillTime;
import com.yw.secondhandtrade.common.enumeration.DBOperationType;
import com.yw.secondhandtrade.pojo.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {

    /**
     * 插入用户数据
     * @param user
     */
    @FillTime(DBOperationType.INSERT)
    void insert(User user);

    /**
     * 根据用户名查询用户
     * @param username
     * @return
     */
    User getByUsername(@Param("username") String username);

    /**
     * 编辑用户信息
     * @param user
     */
    @FillTime(DBOperationType.UPDATE)
    void update(User user);

    /**
     * 由id查询用户信息
     * @param id
     * @return
     */
    User getById(Long id);
}

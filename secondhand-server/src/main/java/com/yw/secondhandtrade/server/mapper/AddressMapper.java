package com.yw.secondhandtrade.server.mapper;

import com.yw.secondhandtrade.pojo.entity.Address;
import com.yw.secondhandtrade.common.enumeration.DBOperationType;
import com.yw.secondhandtrade.server.annotation.FillTime;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;


@Mapper
public interface AddressMapper {

    /**
     * 新增地址
     * @param address
     */
    @FillTime(DBOperationType.INSERT)
    void insert(Address address);

    /**
     * 根据用户ID查询所有地址
     * @param userId
     * @return
     */
    List<Address> listByUserId(Long userId);

    /**
     * 根据ID查询地址
     * @param id
     * @return
     */
    Address getById(Long id);

    /**
     * 修改地址
     * @param address
     */
    @FillTime(DBOperationType.UPDATE)
    void update(Address address);

    /**
     * 根据ID删除地址
     * @param id
     */
    void deleteById(Long id);

    /**
     * 将指定用户的所有地址设置为非默认
     * @param userId
     */
    void setAllToNonDefaultByUserId(Long userId);
}


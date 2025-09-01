package com.yw.secondhandtrade.service;

import com.yw.secondhandtrade.pojo.dto.AddressDTO;
import com.yw.secondhandtrade.pojo.entity.Address;
import java.util.List;

public interface AddressService {

    /**
     * 新增地址
     * @param addressDTO
     */
    void save(AddressDTO addressDTO);

    /**
     * 查询当前用户的所有地址
     * @return
     */
    List<Address> list();

    /**
     * 根据ID查询地址
     * @param id
     * @return
     */
    Address getById(Long id);

    /**
     * 修改地址
     * @param addressDTO
     */
    void update(AddressDTO addressDTO);

    /**
     * 根据ID删除地址
     * @param id
     */
    void deleteById(Long id);

    /**
     * 设置默认地址
     * @param id
     */
    void setDefault(Long id);
}


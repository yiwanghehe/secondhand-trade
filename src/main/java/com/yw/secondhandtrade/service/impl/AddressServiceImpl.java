package com.yw.secondhandtrade.service.impl;

import com.yw.secondhandtrade.common.constant.StatusConstant;
import com.yw.secondhandtrade.common.context.BaseContext;
import com.yw.secondhandtrade.mapper.AddressMapper;
import com.yw.secondhandtrade.pojo.dto.AddressDTO;
import com.yw.secondhandtrade.pojo.entity.Address;
import com.yw.secondhandtrade.service.AddressService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AddressServiceImpl implements AddressService {

    @Autowired
    private AddressMapper addressMapper;

    @Override
    @Transactional
    public void save(AddressDTO addressDTO) {
        Address address = new Address();
        BeanUtils.copyProperties(addressDTO, address);
        address.setUserId(BaseContext.getId());

        // 如果新增的地址是默认地址，需要将该用户其他地址设为非默认
        if (address.getIsDefault() != null && address.getIsDefault().equals(StatusConstant.ENABLE)) {
            addressMapper.setAllToNonDefaultByUserId(address.getUserId());
        }

        addressMapper.insert(address);
    }

    @Override
    public List<Address> list() {
        Long userId = BaseContext.getId();
        return addressMapper.listByUserId(userId);
    }

    @Override
    public Address getById(Long id) {
        return addressMapper.getById(id);
    }

    @Override
    @Transactional
    public void update(AddressDTO addressDTO) {
        Address address = new Address();
        BeanUtils.copyProperties(addressDTO, address);
        address.setUserId(BaseContext.getId()); // 确保更新时也能关联到当前用户

        // 如果将该地址设为默认，需要将该用户其他地址设为非默认
        if (address.getIsDefault() != null && address.getIsDefault().equals(StatusConstant.ENABLE)) {
            addressMapper.setAllToNonDefaultByUserId(address.getUserId());
        }

        addressMapper.update(address);
    }

    @Override
    public void deleteById(Long id) {
        addressMapper.deleteById(id);
    }

    @Override
    @Transactional
    public void setDefault(Long id) {
        Long userId = BaseContext.getId();
        // 将当前用户的所有地址都设置为非默认
        addressMapper.setAllToNonDefaultByUserId(userId);
        // 将指定ID的地址设置为默认
        Address address = Address.builder()
                .id(id)
                .isDefault(StatusConstant.ENABLE)
                .build();
        addressMapper.update(address);
    }
}


package com.yw.secondhandtrade.service.impl;

import com.yw.secondhandtrade.common.constant.MessageConstant;
import com.yw.secondhandtrade.common.constant.StatusConstant;
import com.yw.secondhandtrade.common.context.BaseContext;
import com.yw.secondhandtrade.common.exception.BusinessException;
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

        if (address.getIsDefault() != null && address.getIsDefault().equals(StatusConstant.DEFAULT_ADDRESS)) {
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
        Address address = addressMapper.getById(id);

        // --- 安全校验 ---
        // 检查地址是否存在，以及地址的userId是否与当前登录用户ID匹配
        if (address == null || !address.getUserId().equals(BaseContext.getId())) {
            throw new BusinessException(MessageConstant.ADDRESS_NOT_FOUND_OR_NO_PERMISSION);
        }
        return address;
    }

    @Override
    @Transactional
    public void update(AddressDTO addressDTO) {
        // --- 安全校验 ---
        // 在修改前，先用getById检查权限
        getById(addressDTO.getId());

        Address address = new Address();
        BeanUtils.copyProperties(addressDTO, address);
        address.setUserId(BaseContext.getId());

        if (address.getIsDefault() != null && address.getIsDefault().equals(StatusConstant.DEFAULT_ADDRESS)) {
            addressMapper.setAllToNonDefaultByUserId(address.getUserId());
        }

        addressMapper.update(address);
    }

    @Override
    public void deleteById(Long id) {
        // --- 安全校验 ---
        // 在删除前，先用getById检查权限
        getById(id);
        addressMapper.deleteById(id);
    }

    @Override
    @Transactional
    public void setDefault(Long id) {
        // --- 安全校验 ---
        // 在设置默认前，先用getById检查权限
        getById(id);

        Long userId = BaseContext.getId();
        addressMapper.setAllToNonDefaultByUserId(userId);
        Address address = Address.builder()
                .id(id)
                .isDefault(StatusConstant.DEFAULT_ADDRESS)
                .build();
        addressMapper.update(address);
    }
}


package com.yw.secondhandtrade.controller.user;

import com.yw.secondhandtrade.common.result.Result;
import com.yw.secondhandtrade.pojo.dto.AddressDTO;
import com.yw.secondhandtrade.pojo.entity.Address;
import com.yw.secondhandtrade.service.AddressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/address")
@Tag(name = "【用户端】地址簿接口")
@Slf4j
public class AddressController {

    @Autowired
    private AddressService addressService;

    @PostMapping("/save")
    @Operation(summary = "新增地址")
    public Result save(@RequestBody AddressDTO addressDTO) {
        log.info("新增地址：{}", addressDTO);
        addressService.save(addressDTO);
        return Result.success();
    }

    @GetMapping("/list")
    @Operation(summary = "查询当前用户所有地址")
    public Result<List<Address>> list() {
        log.info("查询当前用户所有地址");
        List<Address> list = addressService.list();
        return Result.success(list);
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据id查询地址")
    public Result<Address> getById(@PathVariable Long id) {
        log.info("根据id查询地址：{}", id);
        Address address = addressService.getById(id);
        return Result.success(address);
    }

    @PutMapping("/update")
    @Operation(summary = "修改地址")
    public Result update(@RequestBody AddressDTO addressDTO) {
        log.info("修改地址：{}", addressDTO);
        addressService.update(addressDTO);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "根据id删除地址")
    public Result deleteById(@PathVariable Long id) {
        log.info("删除地址：{}", id);
        addressService.deleteById(id);
        return Result.success();
    }

    @PutMapping("/default/{id}")
    @Operation(summary = "设置默认地址")
    public Result setDefault(@PathVariable Long id) {
        log.info("设置默认地址：{}", id);
        addressService.setDefault(id);
        return Result.success();
    }
}


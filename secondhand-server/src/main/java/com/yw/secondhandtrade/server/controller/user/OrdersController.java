package com.yw.secondhandtrade.server.controller.user;

import com.yw.secondhandtrade.common.result.PageResult;
import com.yw.secondhandtrade.common.result.Result;
import com.yw.secondhandtrade.pojo.dto.OrdersPageQueryDTO;
import com.yw.secondhandtrade.pojo.dto.OrdersSubmitDTO;
import com.yw.secondhandtrade.pojo.vo.OrderVO;
import com.yw.secondhandtrade.pojo.vo.OrdersSubmitVO;
import com.yw.secondhandtrade.server.service.OrdersService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user/orders")
@Tag(name = "【用户端】订单接口")
@Slf4j
public class OrdersController {

    @Autowired
    private OrdersService ordersService;

    @PostMapping("/submit")
    @Operation(summary = "用户下单")
    public Result<OrdersSubmitVO> submit(@RequestBody OrdersSubmitDTO ordersSubmitDTO) {
        log.info("用户下单：{}", ordersSubmitDTO);
        OrdersSubmitVO ordersSubmitVO = ordersService.submit(ordersSubmitDTO);
        return Result.success(ordersSubmitVO);
    }

    @PutMapping("/pay/{id}")
    @Operation(summary = "模拟支付")
    public Result pay(@PathVariable("id") Long id) {
        log.info("订单支付：{}", id);
        ordersService.pay(id);
        return Result.success();
    }

    @PutMapping("/cancel/{id}")
    @Operation(summary = "取消订单")
    public Result cancel(@PathVariable("id") Long id) {
        log.info("取消订单：{}", id);
        ordersService.cancel(id);
        return Result.success();
    }

    @PutMapping("/confirm/{id}")
    @Operation(summary = "确认收货")
    public Result confirm(@PathVariable("id") Long id) {
        log.info("确认收货：{}", id);
        ordersService.confirm(id);
        return Result.success();
    }

    @GetMapping("/history")
    @Operation(summary = "查询历史订单")
    public Result<PageResult> page(@ParameterObject OrdersPageQueryDTO ordersPageQueryDTO){
        log.info("查询历史订单: {}", ordersPageQueryDTO);
        PageResult pageResult = ordersService.pageQuery(ordersPageQueryDTO);
        return Result.success(pageResult);
    }

    @GetMapping("/details/{id}")
    @Operation(summary = "查询订单详情")
    public Result<OrderVO> details(@PathVariable("id") Long id){
        log.info("查询订单详情: {}", id);
        OrderVO orderVO = ordersService.details(id);
        return Result.success(orderVO);
    }
}

package com.yw.secondhandtrade.controller.user;

import com.yw.secondhandtrade.common.result.Result;
import com.yw.secondhandtrade.pojo.dto.OrdersSubmitDTO;
import com.yw.secondhandtrade.pojo.vo.OrdersSubmitVO;
import com.yw.secondhandtrade.service.OrdersService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
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
}

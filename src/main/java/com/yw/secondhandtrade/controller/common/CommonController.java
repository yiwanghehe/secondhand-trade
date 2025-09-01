package com.yw.secondhandtrade.controller.common;

import com.yw.secondhandtrade.common.result.PageResult;
import com.yw.secondhandtrade.common.result.Result;
import com.yw.secondhandtrade.pojo.dto.GoodsPageQueryDTO;
import com.yw.secondhandtrade.service.GoodsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/common")
@Tag(name = "通用接口")
@Slf4j
public class CommonController {

    @Autowired
    private GoodsService goodsService;

    @GetMapping("/goods/page")
    @Operation(summary = "【公共】商品分页查询")
    public Result<PageResult> page(@ParameterObject GoodsPageQueryDTO goodsPageQueryDTO) {
        log.info("商品分页查询: {}", goodsPageQueryDTO);
        PageResult pageResult = goodsService.pageQueryPublic(goodsPageQueryDTO);
        return Result.success(pageResult);
    }
}

package com.yw.secondhandtrade.controller.user;

import com.yw.secondhandtrade.common.result.Result;
import com.yw.secondhandtrade.pojo.dto.FavoritesDTO;
import com.yw.secondhandtrade.pojo.entity.Goods;
import com.yw.secondhandtrade.service.FavoritesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/favorites")
@Tag(name = "【用户端】收藏夹接口")
@Slf4j
public class FavoritesController {

    @Autowired
    private FavoritesService favoritesService;

    @PostMapping("/add")
    @Operation(summary = "添加收藏")
    public Result add(@RequestBody FavoritesDTO favoritesDTO) {
        log.info("添加收藏：{}", favoritesDTO);
        favoritesService.add(favoritesDTO);
        return Result.success();
    }

    @DeleteMapping("/remove")
    @Operation(summary = "取消收藏")
    public Result remove(@RequestBody FavoritesDTO favoritesDTO) {
        log.info("取消收藏：{}", favoritesDTO);
        favoritesService.remove(favoritesDTO);
        return Result.success();
    }

    @GetMapping("/list")
    @Operation(summary = "查询我的收藏列表")
    public Result<List<Goods>> list() {
        log.info("查询我的收藏列表");
        List<Goods> list = favoritesService.list();
        return Result.success(list);
    }
}

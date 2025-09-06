package com.yw.secondhandtrade.server.controller.user;

import com.yw.secondhandtrade.common.constant.StatusConstant;
import com.yw.secondhandtrade.common.result.Result;
import com.yw.secondhandtrade.pojo.dto.GoodsDTO;
import com.yw.secondhandtrade.pojo.entity.Goods;
import com.yw.secondhandtrade.server.service.GoodsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/goods")
@Tag(name = "【用户端】商品接口")
public class UserGoodsController {

    private static final Logger log = LoggerFactory.getLogger(UserGoodsController.class);

    @Autowired
    private GoodsService goodsService;

    /**
     * 用户发布商品
     * @param goodsDTO
     * @return
     */
    @PostMapping("/publish")
    @Operation(summary = "发布商品")
    public Result publish(@RequestBody GoodsDTO goodsDTO) {
        log.info("用户发布商品：{}", goodsDTO);
        goodsService.publish(goodsDTO);
        return Result.success();
    }

    /**
     * 用户修改自己的商品信息
     * @param goodsDTO
     * @param id
     * @return
     */
    @PutMapping("/update/{id}")
    @Operation(summary = "修改我的商品信息")
    public Result updateMyGoods(@RequestBody GoodsDTO goodsDTO, @PathVariable("id") Long id) {
        goodsDTO.setId(id);
        log.info("用户修改自己的商品：{}", goodsDTO);
        goodsService.updateMyGoods(goodsDTO);
        return Result.success();
    }

    /**
     * 查询我发布的商品
     * @return
     */
    @GetMapping("/my-published")
    @Operation(summary = "查询我发布的商品")
    public Result<List<Goods>> getMyPublishedGoods() {
        log.info("查询我发布的商品");
        List<Goods> list = goodsService.getMyPublished();
        return Result.success(list);
    }

    /**
     * 用户下架自己的商品
     * @param id
     * @return
     */
    @PostMapping("/takedown/{id}")
    @Operation(summary = "下架我的商品")
    public Result takedownMyGoods(@PathVariable Long id) {
        log.info("用户下架自己的商品, id:{}", id);
        goodsService.changeMyGoodsStatus(id, StatusConstant.OFF_SHELF);
        return Result.success();
    }

    /**
     * 用户重新上架自己的商品
     * @param id
     * @return
     */
    @PostMapping("/relist/{id}")
    @Operation(summary = "重新上架我的商品")
    public Result relistMyGoods(@PathVariable Long id) {
        log.info("用户重新上架自己的商品, id:{}", id);
        goodsService.changeMyGoodsStatus(id, StatusConstant.ENABLE);
        return Result.success();
    }
}

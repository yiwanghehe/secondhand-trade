package com.yw.secondhandtrade.controller.admin;

import com.yw.secondhandtrade.common.result.PageResult;
import com.yw.secondhandtrade.common.result.Result;
import com.yw.secondhandtrade.pojo.dto.GoodsDTO;
//import com.yw.secondhandtrade.pojo.dto.GoodsPageQueryDTO;
import com.yw.secondhandtrade.pojo.entity.Goods;
import com.yw.secondhandtrade.service.GoodsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/goods")
@Tag(name = "【管理端】商品管理接口")
public class AdminGoodsController {

    private static final Logger log = LoggerFactory.getLogger(AdminGoodsController.class);

    @Autowired
    private GoodsService goodsService;

    /**
     * 新增商品 (管理端)
     * @param goodsDTO
     * @return
     */
    @PostMapping("/save")
    @Operation(summary = "新增商品")
    public Result save(@RequestBody GoodsDTO goodsDTO){
        log.info("新增商品：{}", goodsDTO);
        goodsService.save(goodsDTO);
        return Result.success();
    }

    /**
     * 根据id查询商品信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    @Operation(summary = "根据id查询商品信息")
    public Result<Goods> getById(@PathVariable Long id){
        log.info("根据id查询商品信息: {}", id);
        Goods goods = goodsService.getById(id);
        return Result.success(goods);
    }

//    /**
//     * 商品分页查询
//     * @param goodsPageQueryDTO
//     * @return
//     */
//    @GetMapping("/page")
//    @Operation(summary = "商品分页查询")
//    public Result<PageResult> page(GoodsPageQueryDTO goodsPageQueryDTO) {
//        log.info("商品分页查询，参数为：{}", goodsPageQueryDTO);
//        PageResult pageResult = goodsService.pageQuery(goodsPageQueryDTO);
//        return Result.success(pageResult);
//    }

    /**
     * 修改商品信息
     * @param goodsDTO
     * @return
     */
    @PutMapping("/update")
    @Operation(summary = "修改商品信息")
    public Result update(@RequestBody GoodsDTO goodsDTO) {
        log.info("修改商品信息：{}", goodsDTO);
        goodsService.update(goodsDTO);
        return Result.success();
    }

    /**
     * 根据id上下架商品
     * @param status 商品状态 0:在售 2:下架
     * @param id 商品id
     * @return
     */
    @PostMapping("/status/{status}")
    @Operation(summary = "根据id上下架商品")
    public Result startOrStop(@PathVariable("status") Integer status, Long id) {
        log.info("修改商品状态：{}, {}", status, id);
        goodsService.startOrStop(status, id);
        return Result.success();
    }

    /**
     * 根据id删除商品
     * @param id
     * @return
     */
    @DeleteMapping("/delete/{id}")
    @Operation(summary = "根据id删除商品")
    public Result deleteById(@PathVariable("id") Long id) {
        log.info("根据id删除商品：{}", id);
        goodsService.deleteById(id);
        return Result.success();
    }
}

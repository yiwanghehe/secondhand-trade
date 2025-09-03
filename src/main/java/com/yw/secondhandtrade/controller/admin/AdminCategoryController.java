package com.yw.secondhandtrade.controller.admin;

import com.yw.secondhandtrade.common.result.PageResult;
import com.yw.secondhandtrade.common.result.Result;
import com.yw.secondhandtrade.pojo.dto.CategoryDTO;
import com.yw.secondhandtrade.pojo.dto.CategoryPageQueryDTO;
import com.yw.secondhandtrade.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/category")
@Tag(name = "【管理端】商品分类接口")
@Slf4j
public class AdminCategoryController {

    @Autowired
    private CategoryService categoryService;

    @PostMapping("/save")
    @Operation(summary = "新增商品分类")
    public Result save(@RequestBody CategoryDTO categoryDTO) {
        log.info("新增商品分类：{}", categoryDTO);
        categoryService.save(categoryDTO);
        return Result.success();
    }

    @PutMapping("/update")
    @Operation(summary = "修改商品分类")
    public Result update(@RequestBody CategoryDTO categoryDTO) {
        log.info("修改商品分类：{}", categoryDTO);
        categoryService.update(categoryDTO);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "根据id删除商品分类")
    public Result deleteById(@PathVariable Long id) {
        log.info("删除商品分类：{}", id);
        categoryService.deleteById(id);
        return Result.success();
    }

    @GetMapping("/page")
    @Operation(summary = "商品分类分页查询")
    public Result<PageResult> page(@ParameterObject CategoryPageQueryDTO categoryPageQueryDTO) {
        log.info("商品分类分页查询：{}", categoryPageQueryDTO);
        PageResult pageResult = categoryService.pageQuery(categoryPageQueryDTO);
        return Result.success(pageResult);
    }

}

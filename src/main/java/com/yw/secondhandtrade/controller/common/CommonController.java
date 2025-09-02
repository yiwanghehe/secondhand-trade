package com.yw.secondhandtrade.controller.common;

import com.yw.secondhandtrade.common.result.PageResult;
import com.yw.secondhandtrade.common.result.Result;
import com.yw.secondhandtrade.common.utils.AliOSSUtils;
import com.yw.secondhandtrade.pojo.dto.GoodsPageQueryDTO;
import com.yw.secondhandtrade.service.GoodsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/common")
@Tag(name = "通用接口")
@Slf4j
public class CommonController {

    @Autowired
    private GoodsService goodsService;

//    不直接注入AliOSSUtils，而是注入它的ObjectProvider, 防止依赖注入时强制注入AliOSSUtils，从而时懒加载OSSClient失效
    @Autowired
    private ObjectProvider<AliOSSUtils> aliOSSUtilsProvider;

    @GetMapping("/goods/page")
    @Operation(summary = "【公共】商品分页查询")
    public Result<PageResult> page(@ParameterObject GoodsPageQueryDTO goodsPageQueryDTO) {
        log.info("商品分页查询: {}", goodsPageQueryDTO);
        PageResult pageResult = goodsService.pageQueryPublic(goodsPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 【公共】文件上传
     * @param file
     * @return
     */
    @PostMapping("/upload")
    @Operation(summary = "【公共】文件上传")
    public Result<String> upload(MultipartFile file) {
        log.info("文件上传: {}", file.getOriginalFilename());

        try {
            AliOSSUtils aliOSSUtils = aliOSSUtilsProvider.getObject();
            String url = aliOSSUtils.upload(file);
            log.info("文件上传成功, 文件访问URL: {}", url);
            return Result.success(url);
        } catch (IOException e) {
            log.error("文件上传失败", e);
        }

        return Result.error("文件上传失败");
    }
}

package com.yw.secondhandtrade.controller.user;

import com.yw.secondhandtrade.common.result.Result;
import com.yw.secondhandtrade.pojo.dto.RatingDTO;
import com.yw.secondhandtrade.service.RatingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user/rating")
@Tag(name = "【用户端】评价接口")
@Slf4j
public class RatingController {

    @Autowired
    private RatingService ratingService;

    @PostMapping("/submit")
    @Operation(summary = "提交评价")
    public Result submit(@RequestBody RatingDTO ratingDTO) {
        log.info("提交评价: {}", ratingDTO);
        ratingService.submit(ratingDTO);
        return Result.success();
    }
}

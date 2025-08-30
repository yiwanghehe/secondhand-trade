package com.yw.secondhandtrade.controller.user;

import com.yw.secondhandtrade.common.constant.JwtClaimsConstant;
import com.yw.secondhandtrade.common.properties.JwtProperties;
import com.yw.secondhandtrade.common.result.Result;
import com.yw.secondhandtrade.common.utils.JwtUtil;
import com.yw.secondhandtrade.pojo.dto.UserDTO;
import com.yw.secondhandtrade.pojo.dto.UserLoginDTO;
import com.yw.secondhandtrade.pojo.entity.User;
import com.yw.secondhandtrade.pojo.vo.UserLoginVO;
import com.yw.secondhandtrade.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;


/**
 * 用户端
 */
@RestController
@RequestMapping("/user/user")
@Slf4j
@Tag(name = "用户端")
public class UserController {

    @Autowired
    UserService userService;

    @Autowired
    JwtProperties jwtProperties;

    /**
     * 注册用户
     *
     * @param userDTO
     * @return
     */
    @PostMapping("/save")
    @Operation(summary = "注册用户")
    public Result save(@RequestBody UserDTO userDTO) {
        log.info("注册用户: {}", userDTO);
        userService.save(userDTO);
        return Result.success();
    }

    /**
     * 登录
     *
     * @param userLoginDTO
     * @return
     */
    @PostMapping("/login")
    @Operation(summary = "登录")
    public Result<UserLoginVO> login(@RequestBody UserLoginDTO userLoginDTO) {
        log.info("用户登录：{}", userLoginDTO);

        User user = userService.login(userLoginDTO);

        //生成jwt令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.USER_ID, user.getId());
        String token = JwtUtil.createJWT(
                jwtProperties.getUserSecretKey(),
                jwtProperties.getUserTtl(),
                claims
        );

        UserLoginVO userLoginVO = UserLoginVO.builder()
                .id(user.getId())
                .userName(user.getUsername())
                .token(token)
                .build();

        return Result.success(userLoginVO);
    }

}

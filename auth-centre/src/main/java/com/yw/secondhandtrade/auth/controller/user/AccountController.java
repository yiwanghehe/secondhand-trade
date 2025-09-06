package com.yw.secondhandtrade.auth.controller.user;

import com.yw.secondhandtrade.auth.service.UserService;
import com.yw.secondhandtrade.common.constant.JwtClaimsConstant;
import com.yw.secondhandtrade.pojo.dto.UserDTO;
import com.yw.secondhandtrade.pojo.dto.UserLoginDTO;
import com.yw.secondhandtrade.pojo.entity.User;
import com.yw.secondhandtrade.common.properties.JwtProperties;
import com.yw.secondhandtrade.common.result.Result;
import com.yw.secondhandtrade.common.utils.JwtUtil;
import com.yw.secondhandtrade.pojo.vo.UserLoginVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user/user")
@Slf4j
@Tag(name = "【用户端】用户接口")
public class AccountController {

    @Autowired
    private UserService userService;
    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 注册
     */
    @PostMapping("/register")
    @Operation(summary = "注册")
    public Result register(@RequestBody UserDTO userDTO) {
        log.info("注册用户: {}", userDTO);
        userService.register(userDTO);
        return Result.success();
    }

    /**
     * 登录
     */
    @PostMapping("/login")
    @Operation(summary = "登录")
    public Result<UserLoginVO> login(@RequestBody UserLoginDTO userLoginDTO) {
        log.info("用户登录：{}", userLoginDTO);

        User user = userService.login(userLoginDTO);

        // 生成jwt令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.USER_ID, user.getId());
        claims.put(JwtClaimsConstant.USER_ROLE, user.getRole());
        String token = JwtUtil.createJWT(
                jwtProperties.getUserSecretKey(),
                jwtProperties.getUserTtl(),
                claims
        );

        UserLoginVO userLoginVO = UserLoginVO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .role(user.getRole())
                .token(token)
                .build();

        return Result.success(userLoginVO);
    }

    /**
     * 编辑当前用户信息
     */
    @PutMapping("/update")
    @Operation(summary = "编辑当前用户信息")
    public Result updateCurrent(@RequestBody UserDTO userDTO) {
        log.info("编辑当前用户信息: {}", userDTO);
        userService.updateCurrent(userDTO);
        return Result.success();
    }
}


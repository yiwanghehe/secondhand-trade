package com.yw.secondhandtrade.controller.admin;

import com.yw.secondhandtrade.common.constant.JwtClaimsConstant;
import com.yw.secondhandtrade.common.constant.MessageConstant;
import com.yw.secondhandtrade.common.constant.RoleConstant;
import com.yw.secondhandtrade.common.properties.JwtProperties;
import com.yw.secondhandtrade.common.result.PageResult;
import com.yw.secondhandtrade.common.result.Result;
import com.yw.secondhandtrade.common.utils.JwtUtil;
import com.yw.secondhandtrade.pojo.dto.AdminUserDTO;
import com.yw.secondhandtrade.pojo.dto.UserDTO;
import com.yw.secondhandtrade.pojo.dto.UserLoginDTO;
import com.yw.secondhandtrade.pojo.dto.UserPageQueryDTO;
import com.yw.secondhandtrade.pojo.entity.User;
import com.yw.secondhandtrade.pojo.vo.UserLoginVO;
import com.yw.secondhandtrade.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/admin/user")
@Slf4j
@Tag(name = "【管理端】用户管理接口")
public class UserManagementController {

    @Autowired
    private UserService userService;
    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 管理端登录
     */
    @PostMapping("/login")
    @Operation(summary = "管理员登录")
    public Result<UserLoginVO> login(@RequestBody UserLoginDTO userLoginDTO){
        log.info("管理员登录：{}", userLoginDTO);

        User user = userService.login(userLoginDTO);

        if(user.getRole().equals(RoleConstant.USER)){
            return Result.error(HttpStatus.FORBIDDEN.value(), MessageConstant.LOGIN_FAILED);
        }

        // 生成JWT令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.USER_ID, user.getId());
        claims.put(JwtClaimsConstant.USER_ROLE, user.getRole()); // 加入角色信息
        String token = JwtUtil.createJWT(
                jwtProperties.getAdminSecretKey(),
                jwtProperties.getAdminTtl(),
                claims
        );

        UserLoginVO userLoginVO = UserLoginVO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .name(user.getName())
                .role(user.getRole())
                .token(token)
                .build();

        return Result.success(userLoginVO);
    }

    /**
     * 新增用户 (可指定角色)
     */
    @PostMapping("/save")
    @Operation(summary = "新增用户")
    public Result save(@RequestBody AdminUserDTO adminUserDTO){
        log.info("新增用户: {}", adminUserDTO);
        userService.save(adminUserDTO);
        return Result.success();
    }

    /**
     * 用户分页查询
     */
    @GetMapping("/page")
    @Operation(summary = "用户分页查询")
    public Result<PageResult> page(@ParameterObject UserPageQueryDTO userPageQueryDTO) {
        log.info("用户分页查询，参数为：{}", userPageQueryDTO);
        PageResult pageResult = userService.pageQuery(userPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 启用禁用账户
     */
    @PostMapping("/status/{status}")
    @Operation(summary = "启用禁用账户")
    public Result startOrStop(@PathVariable Integer status, @RequestParam Long id){
        log.info("启用禁用账户：{}, {}", status, id);
        userService.startOrStop(status, id);
        return Result.success();
    }

    /**
     * 编辑用户信息
     */
    @PutMapping("/update")
    @Operation(summary = "编辑用户信息")
    public Result update(@RequestBody AdminUserDTO adminUserDTO){
        log.info("编辑用户信息: {}", adminUserDTO);
        userService.update(adminUserDTO);
        return Result.success();
    }

    /**
     * 根据ID查询用户信息
     */
    @GetMapping("/{id}")
    @Operation(summary = "根据ID查询用户信息")
    public Result<User> getById(@PathVariable Long id){
        log.info("查询用户信息: {}", id);
        User user = userService.getById(id);
        return Result.success(user);
    }
}


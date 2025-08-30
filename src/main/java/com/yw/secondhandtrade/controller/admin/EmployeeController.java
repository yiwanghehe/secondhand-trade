package com.yw.secondhandtrade.controller.admin;

import com.yw.secondhandtrade.common.constant.JwtClaimsConstant;
import com.yw.secondhandtrade.common.properties.JwtProperties;
import com.yw.secondhandtrade.common.result.PageResult;
import com.yw.secondhandtrade.common.result.Result;
import com.yw.secondhandtrade.common.utils.JwtUtil;
import com.yw.secondhandtrade.pojo.dto.EmployeeDTO;
import com.yw.secondhandtrade.pojo.dto.EmployeeLoginDTO;
import com.yw.secondhandtrade.pojo.dto.EmployeePageQueryDTO;
import com.yw.secondhandtrade.pojo.entity.Employee;
import com.yw.secondhandtrade.pojo.vo.EmployeeLoginVO;
import com.yw.secondhandtrade.service.EmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;


/**
 * 管理端
 */
@RestController
@RequestMapping("/admin/employee")
@Slf4j
@Tag(name = "管理端")
public class EmployeeController {

    @Autowired
    EmployeeService employeeService;

    @Autowired
    JwtProperties jwtProperties;

    /**
     * 注册员工
     * @param employeeDTO
     * @return
     */
    @PostMapping("/save")
    @Operation(summary = "注册员工")
    public Result save(@RequestBody EmployeeDTO employeeDTO){
        log.info("注册员工: {}", employeeDTO);
        employeeService.save(employeeDTO);
        return Result.success();
    }

    /**
     * 登录
     * @param employeeLoginDTO
     * @return
     */
    @PostMapping("/login")
    @Operation(summary = "登录")
    public Result<EmployeeLoginVO> login(@RequestBody EmployeeLoginDTO employeeLoginDTO){
        log.info("员工登录：{}", employeeLoginDTO);

        Employee employee = employeeService.login(employeeLoginDTO);

        //生成jwt令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.EMP_ID, employee.getId());
        String token = JwtUtil.createJWT(
                jwtProperties.getAdminSecretKey(),
                jwtProperties.getAdminTtl(),
                claims
        );

        EmployeeLoginVO employeeLoginVO = EmployeeLoginVO.builder()
                .id(employee.getId())
                .userName(employee.getUsername())
                .name(employee.getName())
                .token(token)
                .build();

        return Result.success(employeeLoginVO);
    }

    /**
     * 员工分页查询
     * @param employeePageQueryDTO
     * @return
     */
    @GetMapping("/page")
    @Operation(summary = "员工分页查询")
    public Result<PageResult> page(@ParameterObject EmployeePageQueryDTO employeePageQueryDTO) {
        log.info("员工分页查询，参数为：{}", employeePageQueryDTO);
        PageResult pageResult = employeeService.pageQuery(employeePageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 修改员工状态
     * @param status
     * @param id
     * @return
     */
    @GetMapping("/status/{status}")
    @Operation(summary = "修改员工状态")
    public Result startOrStop(@PathVariable Integer status, @Param("id") Long id){
        log.info("启用禁用员工账户：{}，{}", status, id);
        employeeService.startOrStop(status, id);
        return Result.success();
    }

    /**
     * 编辑员工信息
     * @param employeeDTO
     * @return
     */
    @PutMapping("/update")
    @Operation(summary = "编辑员工信息")
    public Result update(@RequestBody EmployeeDTO employeeDTO){
        log.info("编辑员工信息: {}", employeeDTO);
        employeeService.update(employeeDTO);
        return Result.success();
    }

    @GetMapping("/{id}")
    @Operation(summary = "由id查询员工信息")
    public Result<Employee> getById(@PathVariable Long id){
        log.info("由id查询员工信息: {}", id);
        Employee employee = employeeService.getById(id);
        return Result.success(employee);
    }

}

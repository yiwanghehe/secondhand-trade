package com.yw.secondhandtrade.service;

import com.yw.secondhandtrade.common.result.PageResult;
import com.yw.secondhandtrade.pojo.dto.EmployeeDTO;
import com.yw.secondhandtrade.pojo.dto.EmployeeLoginDTO;
import com.yw.secondhandtrade.pojo.dto.EmployeePageQueryDTO;
import com.yw.secondhandtrade.pojo.entity.Employee;

public interface EmployeeService {

    /**
     * 注册员工
     * @param employeeDTO
     */
    void save(EmployeeDTO employeeDTO);

    /**
     * 员工登录
     * @param employeeLoginDTO
     * @return
     */
    Employee login(EmployeeLoginDTO employeeLoginDTO);

    /**
     * 员工分页查询
     * @param employeePageQueryDTO
     * @return
     */
    PageResult pageQuery(EmployeePageQueryDTO employeePageQueryDTO);

    /**
     * 修改员工状态
     * @param status
     * @param id
     */
    void startOrStop(Integer status, Long id);

    /**
     * 由id查询员工信息
     * @param id
     * @return
     */
    Employee getById(Long id);

    /**
     * 编辑员工信息
     * @param employeeDTO
     */
    void update(EmployeeDTO employeeDTO);
}

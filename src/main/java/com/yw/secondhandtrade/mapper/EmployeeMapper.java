package com.yw.secondhandtrade.mapper;


import com.github.pagehelper.Page;
import com.yw.secondhandtrade.common.annotation.FillTime;
import com.yw.secondhandtrade.common.enumeration.DBOperationType;
import com.yw.secondhandtrade.pojo.dto.EmployeePageQueryDTO;
import com.yw.secondhandtrade.pojo.entity.Employee;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface EmployeeMapper {

    /**
     * 插入员工数据
     * @param employee
     */
    @FillTime(DBOperationType.INSERT)
    void insert(Employee employee);

    /**
     * 根据用户名查询员工
     * @param username
     * @return
     */
    Employee getByUsername(@Param("username") String username);

    /**
     * 员工分页查询
     * @param employeePageQueryDTO
     * @return
     */
    Page<Employee> pageQuery(EmployeePageQueryDTO employeePageQueryDTO);

    /**
     * 编辑员工信息
     * @param employee
     */
    @FillTime(DBOperationType.UPDATE)
    void update(Employee employee);

    /**
     * 由id查询员工信息
     * @param id
     * @return
     */
    Employee getById(Long id);
}

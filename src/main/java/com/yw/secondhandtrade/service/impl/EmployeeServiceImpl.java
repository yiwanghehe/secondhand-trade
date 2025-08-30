package com.yw.secondhandtrade.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.yw.secondhandtrade.common.constant.MessageConstant;
import com.yw.secondhandtrade.common.constant.StatusConstant;
import com.yw.secondhandtrade.common.exception.AccountLockedException;
import com.yw.secondhandtrade.common.exception.AccountNotFoundException;
import com.yw.secondhandtrade.common.exception.PasswordErrorException;
import com.yw.secondhandtrade.common.result.PageResult;
import com.yw.secondhandtrade.pojo.dto.EmployeeDTO;
import com.yw.secondhandtrade.pojo.dto.EmployeeLoginDTO;
import com.yw.secondhandtrade.pojo.dto.EmployeePageQueryDTO;
import com.yw.secondhandtrade.pojo.entity.Employee;
import com.yw.secondhandtrade.mapper.EmployeeMapper;
import com.yw.secondhandtrade.service.EmployeeService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
public class EmployeeServiceImpl implements EmployeeService {


    @Autowired
    EmployeeMapper employeeMapper;

    /**
     * 注册员工
     * @param employeeDTO
     */
    @Override
    @Transactional
    public void save(EmployeeDTO employeeDTO) {
        Employee employee = new Employee();
        BeanUtils.copyProperties(employeeDTO, employee);

        employee.setStatus(StatusConstant.ENABLE);
        employee.setPassword(DigestUtils.md5DigestAsHex(employeeDTO.getPassword().getBytes()));

        employeeMapper.insert(employee);
    }

    /**
     * 登录
     * @param employeeLoginDTO
     * @return
     */
    @Override
    public Employee login(EmployeeLoginDTO employeeLoginDTO) {
        String username = employeeLoginDTO.getUsername();
        String password = employeeLoginDTO.getPassword();

        //1、根据用户名查询数据库中的数据
        Employee employee = employeeMapper.getByUsername(username);

        //2、处理各种异常情况（用户名不存在、密码不对、账号被锁定）
        if (employee == null) {
            //账号不存在
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        //密码比对
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        if (!password.equals(employee.getPassword())) {
            //密码错误
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }

        if (Objects.equals(employee.getStatus(), StatusConstant.DISABLE)) {
            //账号被锁定
            throw new AccountLockedException(MessageConstant.ACCOUNT_LOCKED);
        }

        //3、返回实体对象
        return employee;
    }

    /**
     * 员工分页查询
     * @param employeePageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(EmployeePageQueryDTO employeePageQueryDTO) {

        PageHelper.startPage(employeePageQueryDTO.getPage(), employeePageQueryDTO.getPageSize());

        Page<Employee> page = employeeMapper.pageQuery(employeePageQueryDTO);

        long total = page.getTotal();
        List<Employee> records = page.getResult();

        return new PageResult(total, records);
    }

    /**
     * 修改员工状态
     * @param status
     * @param id
     */
    @Override
    public void startOrStop(Integer status, Long id) {
        Employee employee = Employee.builder()
                .status(status)
                .id(id)
                .build();
        employeeMapper.update(employee);
    }

    /**
     * 由id查询员工信息
     * @param id
     * @return
     */
    @Override
    public Employee getById(Long id) {
        Employee employee = employeeMapper.getById(id);
        employee.setPassword("null");
        return employee;
    }

    /**
     * 编辑员工信息
     * @param employeeDTO
     */
    @Override
    public void update(EmployeeDTO employeeDTO) {
        Employee employee = new Employee();
        BeanUtils.copyProperties(employeeDTO, employee);

        employee.setPassword(DigestUtils.md5DigestAsHex(employeeDTO.getPassword().getBytes()));

        employeeMapper.update(employee);
    }
}

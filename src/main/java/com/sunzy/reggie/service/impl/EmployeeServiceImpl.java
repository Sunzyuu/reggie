package com.sunzy.reggie.service.impl;

import com.alibaba.druid.util.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sunzy.reggie.domain.Employee;
import com.sunzy.reggie.mapper.EmployeeMapper;
import com.sunzy.reggie.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {

    @Autowired
    private EmployeeMapper employeeMapper;

    public Page<Employee> getPage(int currentPage, int pageSize, String name) {
        IPage<Employee> employeePage = new Page<Employee>(currentPage, pageSize);
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isEmpty(name), Employee::getName, name);
        queryWrapper.orderByDesc(Employee::getUpdateTime);
        Page<Employee> employeeIPage = (Page<Employee>) employeeMapper.selectPage(employeePage, queryWrapper);
        return employeeIPage;
    }
}

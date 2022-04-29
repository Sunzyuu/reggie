package com.sunzy.reggie.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sunzy.reggie.domain.Employee;

public interface EmployeeService extends IService<Employee> {

    public Page<Employee> getPage(int currentPage, int pageSize, String name);
}

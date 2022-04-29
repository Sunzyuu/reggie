package com.sunzy.reggie.controller;


import com.alibaba.druid.util.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sunzy.reggie.common.R;
import com.sunzy.reggie.domain.Employee;
import com.sunzy.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmpolyeeController {

    @Autowired
    private EmployeeService employeeService;

    /**
     * 员工登录
     * @param request
     * @param employee
     * @return
     */
    @PostMapping("login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee){
        /**
         1、将用户密码进行md5加密处理
         2、根据页面提交的用户名username查询数据库
         3、如果没有查询到则返回登录失败结果
         4、密码比对，如果不一致则返回登录失败结果
         5、查看员工状态，如果为已禁用状态，则返回员工已禁用结果
         6、登录成功，将员工id存入Session并返回登录成功结果
         */
        // 1、将用户密码进行md5加密处理
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        // 2、根据页面提交的用户名username查询数据库
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername, employee.getUsername());
        Employee emp = employeeService.getOne(queryWrapper);
        //3、如果没有查询到则返回登录失败结果
        if(emp == null){
            return R.error("登录失败");
        }
        // 4、密码比对，如果不一致则返回登录失败结果
        if(!emp.getPassword().equals(password)){
            return R.error("登录失败");
        }

        // 5、查看员工状态，如果为已禁用状态，则返回员工已禁用结果
        if(emp.getStatus() == 0){
            return R.error("账户已禁用");
        }

        // 6、登录成功，将员工id存入Session并返回登录成功结果
        request.getSession().setAttribute("employee", emp.getId());
        return R.success(emp);
    }

    /**
     * 员工注销
     * @return
     */
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request) {
        request.getSession().removeAttribute("employee");
        return R.success("注销成功");
    }


    /**
     * 添加新员工
     * @param request
     * @param employee
     * @return
     */
    @PostMapping
    public R<String> save(HttpServletRequest request, @RequestBody Employee employee){
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
        employee.setCreateTime(LocalDateTime.now());
        employee.setUpdateTime(LocalDateTime.now());

        long ids = (long) request.getSession().getAttribute("employee");

        employee.setCreateUser(ids);
        employee.setUpdateUser(ids);
        employeeService.save(employee);
        log.info(employee.toString());
        return R.success("添加用户成功！");
    }


    /**
     * 分页查询操作
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> getPage(int page,int pageSize, String name){
        // http://localhost/employee/page?page=1&pageSize=111
        log.info("currentPage:{}, pageSize:{}, name:{}",page, pageSize, name);
        log.info("页面数{}", pageSize);
        Page<Employee> employeePage = new Page<>();
        LambdaQueryWrapper<Employee> qw = new LambdaQueryWrapper<>();
        qw.like(name != null, Employee::getName, name);
        qw.orderByDesc(Employee::getUpdateTime);

        employeeService.page(employeePage, qw);

        return R.success(employeePage);
    }


    /**
     * 禁用用户
     * @param request
     * @param employee
     * @return
     */
    @PutMapping
    public R<String> update(HttpServletRequest request, @RequestBody Employee employee){
        long id = Thread.currentThread().getId();
        log.info("当前线程id: {}", id);
//        long id = (long) request.getSession().getAttribute("employee");
//        employee.setUpdateUser(id);
//        employee.setUpdateTime(LocalDateTime.now());
        employeeService.updateById(employee);

        return R.success("修改用户信息成功！");
    }

    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable long id){
        log.info("getid: {}", id);

        Employee employee = employeeService.getById(id);
        if (employee == null){
            return R.error("没有对应员工信息");
        }
        return R.success(employee);
    }
}

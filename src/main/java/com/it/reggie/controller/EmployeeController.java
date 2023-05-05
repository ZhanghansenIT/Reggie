package com.it.reggie.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.it.reggie.common.R;
import com.it.reggie.entity.Employee;
import com.it.reggie.service.EmployeeService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;

    /**
     * 员工登录
     * @param request
     * @param employee
     * @return
     */

    // 前端发送请求,请求信息中携带了
    // username , password 等
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request , @RequestBody Employee employee){
        //1. 将页面提交的密码进行md5加密处理
        String password = employee.getPassword() ;
        password = DigestUtils.md5DigestAsHex(password.getBytes()) ;
        //2. 根据页面提交的用户名username 查询数据库
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername,employee.getUsername()) ;
        Employee emp = employeeService.getOne(queryWrapper) ;

        //3. 如果没有查询到则返回登录失败的结果

        if(emp == null){
            return R.error("没哟用户名,登录失败") ;
        }
        //4. 密码比对
        if(!emp.getPassword().equals(password)){
            return R.error("密码错误,登录失败");
        }

        //5. 查看员工状态，如果为已经禁用的状态，则返回员工已禁用的结果
        if(emp.getStatus() == 0 ){
            return R.error("账号已禁用,登录失败");
        }
        // 6. 登录成功，将员工id存入session 并返回登录结果
        request.getSession().setAttribute("employee",employee.getId());
        return R.success(emp) ;

    }

    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request){
        // 清理session中保存的已登录的用户的id
        request.getSession().removeAttribute("employee");
        return R.success("退出成功") ;
    }

}
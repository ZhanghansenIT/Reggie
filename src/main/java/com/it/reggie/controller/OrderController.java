package com.it.reggie.controller;


import com.it.reggie.common.R;
import com.it.reggie.entity.Orders;
import com.it.reggie.service.OrderDetailService;
import com.it.reggie.service.OrdersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/order")
public class OrderController {
    @Autowired
    private OrdersService ordersService ;

    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders){
//        orderDetailService.saveOrUpdate().
        ordersService.submit(orders);
        return R.success("提交成功");
    }
}

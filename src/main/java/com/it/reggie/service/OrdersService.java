package com.it.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.it.reggie.entity.Orders;

/**
 *
 */
public interface OrdersService extends IService<Orders> {


    /**
     * 提交用户订单
     * @param orders
     */
    void submit(Orders orders);
}

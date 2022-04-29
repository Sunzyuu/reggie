package com.sunzy.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sunzy.reggie.domain.Orders;

public interface OrderService extends IService<Orders> {

    public void submit(Orders orders);
}

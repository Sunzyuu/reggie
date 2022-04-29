package com.sunzy.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sun.deploy.cache.BaseLocalApplicationProperties;
import com.sunzy.reggie.common.BaseContext;
import com.sunzy.reggie.common.CustomException;
import com.sunzy.reggie.domain.*;
import com.sunzy.reggie.mapper.OrderMapper;
import com.sunzy.reggie.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Orders> implements OrderService {


    @Autowired
    private ShoppingCartService shoppingCartService;

    @Autowired
    private UserService userService;

    @Autowired
    private AddressBookService addressService;

    @Autowired
    private OrderDetailService orderDetailService;


    @Override
    public void submit(Orders orders) {
        // 查询当前用户id
        Long id = BaseContext.getCurrentId();

        // 查询该用户购物车数据
        LambdaQueryWrapper<ShoppingCart> shoppingCartLambdaQueryWrapper = new LambdaQueryWrapper<>();
        shoppingCartLambdaQueryWrapper.eq(ShoppingCart::getUserId, id);
        List<ShoppingCart> shoppingCartsList = shoppingCartService.list(shoppingCartLambdaQueryWrapper);

        if(shoppingCartsList == null || shoppingCartsList.size() == 0){
            throw new CustomException("购物车为空，不可以下单！");
        }

        // 获取用户数据
        User user = userService.getById(id);

        // 获取地址信息
        AddressBook addressBook = addressService.getById(orders.getAddressBookId());
        if(addressBook == null){
            throw new CustomException("请添加地址后下单！");
        }
        // 插入订单数据 一条
        AtomicInteger amount = new AtomicInteger(0); // 元子操作保证线程安全
        long orderId = IdWorker.getId();  //订单号


        List<OrderDetail> orderDetailList = shoppingCartsList.stream().map((item) -> {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrderId(orderId);
            orderDetail.setNumber(item.getNumber());
            orderDetail.setDishFlavor(item.getDishFlavor());
            orderDetail.setDishId(item.getDishId());
            orderDetail.setSetmealId(item.getSetmealId());
            orderDetail.setName(item.getName());
            orderDetail.setImage(item.getImage());
            orderDetail.setAmount(item.getAmount());
            amount.addAndGet(item.getAmount().multiply(new BigDecimal(item.getNumber())).intValue());
            return orderDetail;
        }).collect(Collectors.toList());



        orders.setId(orderId);
        orders.setOrderTime(LocalDateTime.now());
        orders.setCheckoutTime(LocalDateTime.now());
        orders.setStatus(2);
        orders.setAmount(new BigDecimal(amount.get()));//总金额
        orders.setUserId(id);
        orders.setNumber(String.valueOf(orderId));
        orders.setUserName(user.getName());
        orders.setConsignee(addressBook.getConsignee());
        orders.setPhone(addressBook.getPhone());
        orders.setAddress((addressBook.getProvinceName() == null ? "" : addressBook.getProvinceName())
                + (addressBook.getCityName() == null ? "" : addressBook.getCityName())
                + (addressBook.getDistrictName() == null ? "" : addressBook.getDistrictName())
                + (addressBook.getDetail() == null ? "" : addressBook.getDetail()));
        //向订单表插入数据，一条数据
        this.save(orders);
        // 插入订单明细数据 可能是多条
        orderDetailService.saveBatch(orderDetailList);
        // 清空购物车
        shoppingCartService.remove(shoppingCartLambdaQueryWrapper);
    }
}

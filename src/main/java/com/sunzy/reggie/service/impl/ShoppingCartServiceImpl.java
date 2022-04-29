package com.sunzy.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sunzy.reggie.domain.ShoppingCart;
import com.sunzy.reggie.mapper.ShoppingCarMapper;
import com.sunzy.reggie.service.ShoppingCartService;
import org.springframework.stereotype.Service;

@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCarMapper, ShoppingCart> implements ShoppingCartService {
}

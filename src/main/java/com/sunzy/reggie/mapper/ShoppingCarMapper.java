package com.sunzy.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sunzy.reggie.domain.ShoppingCart;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ShoppingCarMapper extends BaseMapper<ShoppingCart> {
}

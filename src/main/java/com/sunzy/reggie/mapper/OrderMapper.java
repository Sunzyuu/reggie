package com.sunzy.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sunzy.reggie.domain.Orders;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderMapper extends BaseMapper<Orders> {
}

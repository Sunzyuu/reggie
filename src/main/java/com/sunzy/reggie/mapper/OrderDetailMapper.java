package com.sunzy.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sunzy.reggie.domain.OrderDetail;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderDetailMapper extends BaseMapper<OrderDetail> {
}

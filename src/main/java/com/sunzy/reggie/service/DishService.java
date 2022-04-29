package com.sunzy.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sunzy.reggie.domain.Dish;
import com.sunzy.reggie.dto.DishDto;

public interface DishService extends IService<Dish> {

    // 新增菜品，同时插入菜品对应的口味数据
    public void saveWithFlavor(DishDto dishDto);


    // 更新菜品，同时插入菜品对应的口味数据
    public void updateWithFlavor(DishDto dishDto);
}

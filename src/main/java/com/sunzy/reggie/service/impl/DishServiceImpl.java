package com.sunzy.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sunzy.reggie.domain.Dish;
import com.sunzy.reggie.domain.DishFlavor;
import com.sunzy.reggie.dto.DishDto;
import com.sunzy.reggie.mapper.DishMapper;
import com.sunzy.reggie.service.DishFlavorService;
import com.sunzy.reggie.service.DishService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Autowired
    private DishFlavorService dishFlavorService;

    //    @Override

    /**
     * 保存菜品
     * @param dishDto
     */
    @Transactional
    public void saveWithFlavor(DishDto dishDto) {
        //保存基本信息到dish表中
        this.save(dishDto);
        Long id = dishDto.getId();
        List<DishFlavor> flavors = dishDto.getFlavors();


        flavors = flavors.stream().map((item) ->{
            item.setDishId(id);
            return item;
        }).collect(Collectors.toList());

        // 保存口味数据到dish_flavor中
        dishFlavorService.saveBatch(flavors);
    }

    @Transactional
    public void updateWithFlavor(DishDto dishDto) {
        //保存基本信息到dish表中
        this.updateById(dishDto);

        //首先删除flavor表中的信息再保存
        LambdaQueryWrapper<DishFlavor> qw = new LambdaQueryWrapper<>();
        qw.eq(DishFlavor::getDishId, dishDto.getId());
        dishFlavorService.remove(qw);

        //保存新的flavor数据
        List<DishFlavor> flavors = dishDto.getFlavors();


        flavors = flavors.stream().map((item) ->{
            item.setDishId(dishDto.getId());
            return item;
        }).collect(Collectors.toList());

        // 批量保存口味数据到dish_flavor中
        dishFlavorService.saveBatch(flavors);
    }
}

package com.sunzy.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sunzy.reggie.common.CustomException;
import com.sunzy.reggie.domain.Category;
import com.sunzy.reggie.domain.Dish;
import com.sunzy.reggie.domain.Setmeal;
import com.sunzy.reggie.mapper.CategoryMapper;
import com.sunzy.reggie.service.CategoryService;
import com.sunzy.reggie.service.DishService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Autowired
    private DishService dishService;

    @Autowired
    private SetMealServiceImpl setMealService;

    @Override
    public void remove(Long id) {
        LambdaQueryWrapper<Dish> qw1 = new LambdaQueryWrapper<>();
        qw1.eq(Dish::getCategoryId, id);

        int count1 = dishService.count(qw1);
        if(count1 > 0){
            // 抛出业务异常错误
            throw new CustomException("该分类关联菜品，不能被删除！");
        }


        LambdaQueryWrapper<Setmeal> qw2 = new LambdaQueryWrapper<>();
        qw2.eq(Setmeal::getCategoryId, id);
        int count2 = setMealService.count(qw2);
        if(count2 > 0){
            // 抛出业务异常
            throw new CustomException("该分类关联套餐，不能被删除！");
        }

        super.removeById(id);

    }
}

package com.sunzy.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sunzy.reggie.common.CustomException;
import com.sunzy.reggie.domain.Dish;
import com.sunzy.reggie.domain.Setmeal;
import com.sunzy.reggie.domain.SetmealDish;
import com.sunzy.reggie.dto.SetmealDto;
import com.sunzy.reggie.mapper.SetmealMapper;
import com.sunzy.reggie.service.SetMealDishService;
import com.sunzy.reggie.service.SetMealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SetMealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetMealService {

    @Autowired
    private SetMealDishService setMealDishService;


    @Autowired
    private SetMealService setMealService;

    @Transactional
    @Override
    public void saveSetmeal(SetmealDto setmealDto) {
        // 首先保存套餐的基本信息
        setMealService.save(setmealDto);
        Long id = setmealDto.getId();
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        // 对套餐中的菜品进行处理，加上套餐的id
        setmealDishes = setmealDishes.stream().map((item) -> {
            item.setSetmealId(id);
            return item;
        }).collect(Collectors.toList());
        // 保存套餐中的菜品
        setMealDishService.saveBatch(setmealDishes);
    }


    /**
     * 删除套餐 并删除与该套擦关联的菜品数据
     * @param ids
     */
    @Transactional
    @Override
    public void removeSetmealWhitDish(List<Long> ids) {
        // 查询套餐状态是否可以删除
        LambdaQueryWrapper<Setmeal> qw = new LambdaQueryWrapper<>();
        qw.in(Setmeal::getId, ids);
        // 售卖中是套餐不可以删除
        qw.eq(Setmeal::getStatus, 1);
        int count = this.count(qw);
        // 不能删除则抛出业务异常
        if(count > 0){
            throw new CustomException("套餐正在售卖中，不可以删除！");
        }

        // 可以删除则进行对应操作
        this.removeByIds(ids);
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(SetmealDish::getSetmealId, ids);
        setMealDishService.remove(queryWrapper);

    }
}

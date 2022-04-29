package com.sunzy.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sunzy.reggie.domain.Setmeal;
import com.sunzy.reggie.dto.SetmealDto;

import java.util.List;

public interface SetMealService extends IService<Setmeal> {

    public void saveSetmeal(SetmealDto setmealDto);

    public void removeSetmealWhitDish(List<Long> ids);
}

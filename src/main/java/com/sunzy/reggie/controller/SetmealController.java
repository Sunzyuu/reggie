package com.sunzy.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sunzy.reggie.common.R;
import com.sunzy.reggie.domain.Category;
import com.sunzy.reggie.domain.Dish;
import com.sunzy.reggie.domain.Setmeal;
import com.sunzy.reggie.domain.SetmealDish;
import com.sunzy.reggie.dto.SetmealDto;
import com.sunzy.reggie.mapper.SetmealDishMapper;
import com.sunzy.reggie.service.CategoryService;
import com.sunzy.reggie.service.DishService;
import com.sunzy.reggie.service.SetMealDishService;
import com.sunzy.reggie.service.SetMealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/setmeal")
@Slf4j
public class SetmealController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private SetMealService setMealService;

    @Autowired
    private DishService dishService;

    @Autowired
    private SetMealDishService setMealDishService;

    @PostMapping
    public R<String> save(@RequestBody SetmealDto setmealDto){
        log.info(setmealDto.toString());

        setMealService.saveSetmeal(setmealDto);
        return R.success("添加套餐成功！");
    }


    /**
     * 套餐的分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name){
        Page<SetmealDto> dtoPage = new Page<>(page, pageSize);
        Page<Setmeal> setmealPage = new Page<Setmeal>();
        LambdaQueryWrapper<Setmeal> qw = new LambdaQueryWrapper<>();
        qw.like(name != null, Setmeal::getName, name);
        qw.orderByDesc(Setmeal::getUpdateTime);
        setMealService.page(setmealPage,qw);

        BeanUtils.copyProperties(setmealPage, dtoPage,"records");
        // 在dtoPage中设置records 首先查询到相关菜品
        List<Setmeal> records = setmealPage.getRecords();
        List<SetmealDto> setmealDtos = records.stream().map((item) -> {
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(item, setmealDto);

            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);
            String categoryName = category.getName();
            if (categoryName != null) {
                setmealDto.setCategoryName(categoryName);
            }

            return setmealDto;
        }).collect(Collectors.toList());

        dtoPage.setRecords(setmealDtos);

        return R.success(dtoPage);
    }

    /**
     * 删除套餐
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids){

        setMealService.removeSetmealWhitDish(ids);
        return R.success("删除成功！");
    }

    /**
     * 获取套餐信息
     * @param setmeal
     * @return
     */
    @GetMapping("/list")
    public R<List<SetmealDto>> list(Setmeal setmeal){

        LambdaQueryWrapper<Setmeal> qw = new LambdaQueryWrapper<>();
        qw.eq(Setmeal::getCategoryId, setmeal.getCategoryId());
        qw.eq(Setmeal::getStatus, setmeal.getStatus());
        List<Setmeal> setmealList = setMealService.list(qw);
        List<SetmealDto> setmealDtoList = setmealList.stream().map((item) ->{
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(item, setmealDto);
            Long id = item.getId();
            LambdaQueryWrapper<SetmealDish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
            dishLambdaQueryWrapper.eq(id != null, SetmealDish::getId, id);
            List<SetmealDish> dishList = setMealDishService.list(dishLambdaQueryWrapper);
            setmealDto.setSetmealDishes(dishList);
            return setmealDto;
        }).collect(Collectors.toList());
        return R.success(setmealDtoList);
    }


    /**
     * 查看套餐详情
     * @param id
     * @return
     */
    @GetMapping("/dish/{id}")
    public R<List<SetmealDish>> dish(@PathVariable Long id){
        LambdaQueryWrapper<SetmealDish> qw = new LambdaQueryWrapper<>();
        qw.eq(SetmealDish::getSetmealId, id);

        List<SetmealDish> setmealDishList = setMealDishService.list(qw);


        return R.success(setmealDishList);
    }



}

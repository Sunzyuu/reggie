package com.sunzy.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sunzy.reggie.common.R;
import com.sunzy.reggie.domain.Category;
import com.sunzy.reggie.domain.Dish;
import com.sunzy.reggie.domain.DishFlavor;
import com.sunzy.reggie.dto.DishDto;
import com.sunzy.reggie.service.CategoryService;
import com.sunzy.reggie.service.DishFlavorService;
import com.sunzy.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/dish")
public class DishController {
    @Autowired
    private DishService dishService;

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;



    /**
     * 新增菜品
     * @param dishDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto){
        log.info(dishDto.toString());
        dishService.saveWithFlavor(dishDto);
        // 删除菜品的缓存数据
        Set keys = redisTemplate.keys("dish_*");
        redisTemplate.delete(keys);
        return R.success("添加成功！");
    }


    /**
     * 新增菜品
     * @param dishDto
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto){
        log.info(dishDto.toString());
        dishService.updateWithFlavor(dishDto);
        // 删除菜品的缓存数据
        String key = "dish_" + dishDto.getCategoryId() + "_1";
        redisTemplate.delete(key);
        return R.success("更新成功！");
    }

    /**
     * 菜品分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name){
        Page<Dish> dishPage = new Page<>(page, pageSize);
        Page<DishDto> dishDtoPage = new Page<DishDto>();

        LambdaQueryWrapper<Dish> qw = new LambdaQueryWrapper<>();
        qw.like(name != null,Dish::getName, name);
        qw.orderByDesc(Dish::getSort);
        dishService.page(dishPage, qw);

        // 拷贝dishPage中的属性，然后对records的数据进行处理
        BeanUtils.copyProperties(dishPage, dishDtoPage, "records");

        List<Dish> records = dishPage.getRecords();
        List<DishDto> collect = records.stream().map((item) -> {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item, dishDto);
            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);
            if(category != null) {
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }

            return dishDto;
        }).collect(Collectors.toList());

        dishDtoPage.setRecords(collect);
        return R.success(dishDtoPage);
    }

    /**
     * 获取单个菜品信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<DishDto> getById(@PathVariable Long id) {

        Dish dish = dishService.getById(id);
        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish, dishDto);
        LambdaQueryWrapper<DishFlavor> qw = new LambdaQueryWrapper<>();
        qw.eq(DishFlavor::getDishId, id);
        List<DishFlavor> list = dishFlavorService.list(qw);
        dishDto.setFlavors(list);
        Long categoryId = dish.getCategoryId();
        Category category = categoryService.getById(categoryId);
        if(category != null){
            String categoryName = category.getName();
            dishDto.setCategoryName(categoryName);
        }
        return R.success(dishDto);
    }


    /**
     * @param dish
     * @return
     */
/*    @GetMapping("/list")
    public R<List<Dish>> getById(Dish dish){
        LambdaQueryWrapper<Dish> qw = new LambdaQueryWrapper<>();
        qw.eq(dish.getCategoryId() != null, Dish::getCategoryId, dish.getCategoryId());
        // 添加条件 菜品状态为在售
        qw.eq(Dish::getStatus, 1);
        qw.orderByDesc(Dish::getSort);

        List<Dish> list = dishService.list(qw);
        return R.success(list);
    }*/
    @GetMapping("/list")
    public R<List<DishDto>> getById(Dish dish){
        List<DishDto> dishDtoList = null;
        // 为菜品列表优化 将查询到的数据添加到redis中
        // 1.查看缓存中是否存在数据
        String key = "dish_" + dish.getCategoryId() + "_" + dish.getStatus(); // dish_132322323221122_1
        dishDtoList = (List<DishDto>) redisTemplate.opsForValue().get(key);

        // 2.如果存在直接获取数据返回
        if(dishDtoList != null){
            return R.success(dishDtoList);
        }
        // 3.如果不存在则到数据库中查询返回 并将数据保存到redis

        LambdaQueryWrapper<Dish> qw = new LambdaQueryWrapper<>();
        qw.eq(dish.getCategoryId() != null, Dish::getCategoryId, dish.getCategoryId());
        // 添加条件 菜品状态为在售
        qw.eq(Dish::getStatus, 1);
        qw.orderByDesc(Dish::getSort);

        List<Dish> list = dishService.list(qw);
        dishDtoList = list.stream().map((item) ->{
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item, dishDto);
            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);
            if(category != null) {
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }


            Long dishId = item.getId();
            LambdaQueryWrapper<DishFlavor> dishqw = new LambdaQueryWrapper<>();
            dishqw.eq(DishFlavor::getDishId, dishId);
            List<DishFlavor> dishFlavorList = dishFlavorService.list(dishqw);
            dishDto.setFlavors(dishFlavorList);
            return dishDto;
        }).collect(Collectors.toList());

        // 将查询到的数据保存到数据库中
        redisTemplate.opsForValue().set(key, dishDtoList, 60, TimeUnit.MINUTES);
        return R.success(dishDtoList);
    }

}

package com.sunzy.reggie.dto;

import com.sunzy.reggie.domain.Setmeal;
import com.sunzy.reggie.domain.SetmealDish;
import lombok.Data;
import java.util.List;

/**
 * 套餐与菜品关系
 */
@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}

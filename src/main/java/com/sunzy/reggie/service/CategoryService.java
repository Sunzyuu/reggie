package com.sunzy.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sunzy.reggie.domain.Category;


public interface CategoryService extends IService<Category> {
    public void remove(Long id);
}

package com.it.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.it.reggie.dto.DishDto;
import com.it.reggie.entity.Dish;
import com.it.reggie.mapper.DishMapper;
import org.springframework.stereotype.Service;


public interface DishService extends IService<Dish> {

    // 新增菜品 ,同时插入菜品对应的口味数据,需要操作的表格
    // dish  ,dish_flavor

    public void saveWithFlavor(DishDto dishDto) ;

    // 根据Id 查询dishDTO和对应甜味
    public DishDto getByIdWithFlavor(Long id )  ;

    // 更新
    public void updateWithFlavor(DishDto dishDto) ;
}

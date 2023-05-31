package com.it.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.it.reggie.dto.DishDto;
import com.it.reggie.entity.Dish;
import com.it.reggie.entity.DishFlavor;
import com.it.reggie.mapper.DishMapper;
import com.it.reggie.service.DishFlavorService;
import com.it.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DishServiceImpl  extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Autowired
    private DishFlavorService dishFlavorService ;
    /**
     * 新增菜品,同时保存对应的口味
     * @param dishDto
     */

    @Transactional
    @Override
    public void saveWithFlavor(DishDto dishDto) {
        // 保存菜品的基本信息
        this.save(dishDto) ;
        Long dishId = dishDto.getId() ;
        List<DishFlavor> dishFlavors =   dishDto.getFlavors() ;
        dishFlavors = dishFlavors.stream().map((item) -> {
            // 给每一个注入id 的属性值
            item.setDishId(dishId);
            return item ;
        }).collect(Collectors.toList());
        dishFlavorService.saveBatch(dishFlavors) ;
    }

    @Override
    public DishDto getByIdWithFlavor(Long id) {
        // 查询菜品的基本信息
        Dish dish = this.getById(id)  ;
        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish,dishDto);



        // 查询菜品的对应的口味信息
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>()  ;
        queryWrapper.eq(DishFlavor::getDishId,dish.getId()) ;
        List<DishFlavor> flavors = dishFlavorService.list(queryWrapper) ;
        dishDto.setFlavors(flavors);

        return null;
    }
}

package com.it.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.it.reggie.common.CustomException;
import com.it.reggie.entity.Category;
import com.it.reggie.entity.Dish;
import com.it.reggie.entity.Setmeal;
import com.it.reggie.mapper.CategoryMapper;
import com.it.reggie.service.CategoryService;
import com.it.reggie.service.DishService;
import com.it.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Autowired
    private DishService dishService ;
    @Autowired
    private SetmealService setmealService ;
    /**
     * 根据id删除,在删除之前判断分类是否关联了菜品或者套餐
     *
     * @param id
     */

    @Override
    public void remove(Long id) {

        // 查询当前分类是否关联了菜品,如果已经关联,抛出一个业务异常
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>() ;
        // 添加查询条件
        dishLambdaQueryWrapper.eq(Dish::getCategoryId,id) ;
        int count1 =  dishService.count(dishLambdaQueryWrapper) ;
        if(count1 >0 ) {
            // 说明已经关联了菜品 ,抛出一个异常
            throw new CustomException("当前分类关联了菜品,不能删除") ;

        }
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>() ;
        //添加查询条件
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId,id) ;
        int count2 = setmealService.count(setmealLambdaQueryWrapper) ;
        if(count2 > 0 ){
            // 说明已经关联套餐
            throw new CustomException("当前分类关联了套餐,不能删除") ;
        }
        super.removeById(id) ;
     }
}

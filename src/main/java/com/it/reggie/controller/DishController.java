package com.it.reggie.controller;



import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.it.reggie.common.R;
import com.it.reggie.dto.DishDto;
import com.it.reggie.dto.SetmealDto;
import com.it.reggie.entity.Category;
import com.it.reggie.entity.Dish;
import com.it.reggie.entity.DishFlavor;
import com.it.reggie.service.CategoryService;
import com.it.reggie.service.DishFlavorService;
import com.it.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/dish")

public class DishController {
    @Autowired
    private DishService dishService ;

    @Autowired
    private DishFlavorService dishFlavorService ;

    @Autowired
    private CategoryService categoryService ;
    /**
     * 新增菜品
     * @return
     */
    @PostMapping()
    public R<String> save(@RequestBody DishDto dishDto){
        log.info(dishDto.toString()) ;
        dishService.saveWithFlavor(dishDto);

        return R.success("success") ;
    }

    /**
     * 菜品信息的分页
     * @param page
     * @param pageSize
     * @param name
     * @return
     */

    @GetMapping("/page")
    public R<Page> page(int page , int pageSize , String name){

        // 构造分页构造器
        Page<Dish> pageInfo = new Page(page,pageSize) ;
        Page<DishDto> dishDtoPage = new Page<>()  ;
        // 条件构造器
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>() ;
        // 添加过滤条件

        queryWrapper.like(name!=null ,Dish::getName,name) ;

        // 排序
        queryWrapper.orderByDesc(Dish::getUpdateTime) ;
        // 执行分页查询
        dishService.page(pageInfo,queryWrapper) ;

        // 对象拷贝 (不拷贝 records 属性)
        BeanUtils.copyProperties(pageInfo,dishDtoPage,"records");
        List<Dish> records = pageInfo.getRecords() ;
        List<DishDto> list = records.stream().map((item)-> {
            DishDto dishDto = new DishDto() ;
            BeanUtils.copyProperties(item,dishDto);

            // 分类id
            Long categoryId = item.getCategoryId() ;
            // 根据id 查询分类对象
            Category category = categoryService.getById(categoryId);

            if(category!=null){
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }

            return dishDto ;
        }).collect(Collectors.toList()) ;

        dishDtoPage.setRecords(list) ;

        return R.success(dishDtoPage) ;
    }

    /**
     * 根据id查询 菜品信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<DishDto> get(@PathVariable("id") Long id ){

        DishDto  dishDto =  dishService.getByIdWithFlavor(id) ;

        log.info(dishDto.toString());

        return R.success(dishDto);
    }

    /**
     * 修改菜品
     * @param dishDto
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto){
        log.info(dishDto.toString());
        dishService.updateWithFlavor(dishDto)  ;

        return R.success("修改菜品成功" ) ;
    }

    /**
     * 根据条件查询对应的菜品数据
     * @param dish
     * @return
     */
//    @GetMapping("/list")
//    public R<List<Dish>> list( Dish dish){
//
//        // 构造查询查询条件
//        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>() ;
//        // 添加排序条件
//        queryWrapper.eq(dish.getCategoryId() !=null ,Dish::getCategoryId,dish.getCategoryId()) ;
//        queryWrapper.eq(Dish::getStatus,1) ;// 查询状态是1 (起售状态,0表示停售) ;
//
//        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime) ;
//        List<Dish> dishList =  dishService.list(queryWrapper) ;
//
//
//        return R.success(dishList);
//    }

    @GetMapping("/list")
    public R<List<DishDto>> list( Dish dish){

        // 构造查询查询条件
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>() ;
        // 添加排序条件
        queryWrapper.eq(dish.getCategoryId() !=null ,Dish::getCategoryId,dish.getCategoryId()) ;
        queryWrapper.eq(Dish::getStatus,1) ;// 查询状态是1 (起售状态,0表示停售) ;

        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime) ;
        List<Dish> List =  dishService.list(queryWrapper) ;


        List<DishDto> dishDtoList = List.stream().map((item)-> {
            DishDto dishDto = new DishDto() ;
            BeanUtils.copyProperties(item,dishDto);

            // 分类id
            Long categoryId = item.getCategoryId() ;
            // 根据id 查询分类对象
            Category category = categoryService.getById(categoryId);

            if(category!=null){
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }
            // 当前菜品的id
            Long dishId = item.getId() ;

            LambdaQueryWrapper<DishFlavor> queryWrapper1 = new LambdaQueryWrapper<>() ;
            queryWrapper1.eq(DishFlavor::getDishId,dishId) ;
            List<DishFlavor> dishFlavorList= dishFlavorService.list(queryWrapper1) ;

            dishDto.setFlavors(dishFlavorList);
            return dishDto ;
        }).collect(Collectors.toList()) ;


        return R.success(dishDtoList);
    }

}

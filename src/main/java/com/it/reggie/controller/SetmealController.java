package com.it.reggie.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.it.reggie.common.R;
import com.it.reggie.dto.SetmealDto;
import com.it.reggie.entity.Category;
import com.it.reggie.entity.Setmeal;
import com.it.reggie.entity.SetmealDish;
import com.it.reggie.service.CategoryService;
import com.it.reggie.service.SetmealDishService;
import com.it.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 套餐管理
 */
@Slf4j
@RestController
@RequestMapping("/setmeal")
public class SetmealController {

    @Autowired
    private SetmealDishService setmealDishService;
    @Autowired
    private SetmealService setmealService;
    @Autowired
    private CategoryService categoryService ;
    /**
     * 新增套餐
     *
     * @param setmealDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody SetmealDto setmealDto) {

        log.info("套餐信息 :{}", setmealDto);
        setmealService.saveWithDish(setmealDto);

        return R.success("新增套餐信息成功");
    }

    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {
        // 分页构造器

        Page<Setmeal> pageInfo = new Page<>(page, pageSize);
        Page<SetmealDto> dtoPage = new Page<>() ;


        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        // 添加查询 ,根据name 模糊查询
        queryWrapper.like(name!=null,Setmeal::getName,name) ;

        // 添加排序条件
        queryWrapper.orderByDesc(Setmeal::getUpdateTime) ;
        setmealService.page(pageInfo,queryWrapper) ;
        BeanUtils.copyProperties(pageInfo,dtoPage,"records");

        List<Setmeal> records = pageInfo.getRecords();
        List<SetmealDto> list = records.stream().map((item)->{

            SetmealDto setmealDto = new SetmealDto() ;

            BeanUtils.copyProperties(item,setmealDto);
            // 分类id
            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId) ;
            if(category!=null) {
                String categoryName = category.getName() ;
                setmealDto.setCategoryName(categoryName) ;
            }
            return setmealDto;
        }).collect(Collectors.toList()) ;

        dtoPage.setRecords(list) ;

        return R.success(dtoPage);
    }

    // 修改
//    public R<SetmealDto> update(Category category ){
//        Long catgegoryId = category.getId() ;
//
//
//        return null ;
//    }
    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids){
        log.info("ids: {} " ,ids);
        setmealService.removeWithDish(ids);
        return R.success("套餐数据删除成功");
    }

    /**
     * 对套餐进行停售
     * @param status
     * @param ids
     * @return
     */
    @PostMapping("/status/{status}")
    public R<String> stop(@PathVariable Integer status,Long []ids){
        // 根据套餐id修改
        for(int i = 0 ; i<ids.length; i++){

            Setmeal setmeal = setmealService.getById(ids[i]) ;
            setmeal.setStatus(status);
            // 修改状态
            setmealService.updateById(setmeal) ;
        }

        return R.success("修改成功") ;
    }


    @PutMapping
    public R<SetmealDto> update(@RequestBody SetmealDto setmealDto){
        log.info("修改套餐信息");
        setmealService.updateWithDish(setmealDto); ;
        return R.success(setmealDto) ;
    }


    @GetMapping("/{id}")
    public R<SetmealDto> getById(@PathVariable Long id) {
        log.info("根据id查询套餐信息:{}", id);
        // 调用service执行查询
        SetmealDto setmealDto = setmealService.getByIdWithDish(id);
        return R.success(setmealDto);
    }

    /**
     * 根据条件查询数据
     * @param setmeal
     * @return
     */
    @GetMapping("/list")
    public R<List<Setmeal>> list( Setmeal setmeal) {
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper() ;
        queryWrapper.eq(setmeal.getCategoryId() !=null,Setmeal::getCategoryId,setmeal.getCategoryId()) ;
        queryWrapper.eq(setmeal.getStatus()!=null,Setmeal::getStatus,setmeal.getStatus())  ;
        //排序
        queryWrapper.orderByDesc(Setmeal::getUpdateTime)  ;
        List<Setmeal> setmealList = setmealService.list(queryWrapper);
        return R.success(setmealList);
    }
}

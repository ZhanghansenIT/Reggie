package com.it.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.it.reggie.common.CustomException;
import com.it.reggie.dto.SetmealDto;
import com.it.reggie.entity.Setmeal;
import com.it.reggie.entity.SetmealDish;
import com.it.reggie.mapper.SetmealMapper;
import com.it.reggie.service.SetmealDishService;
import com.it.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {


    @Autowired
    private SetmealDishService setmealDishService ;
    /**
     * 新增套餐,同时需要保存套餐和菜品的关联关系
     *
     */
    @Transactional
    @Override
    public void saveWithDish(SetmealDto setmealDto) {
        //  保存套餐的基本信息
        this.save(setmealDto) ;
        // 保存套餐和菜品的关联信息,操作setmealdish ,执行iunsert操作

        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes() ;
        setmealDishes.stream().map((item)->{
            item.setSetmealId(setmealDto.getId());
            return item  ;
        }).collect(Collectors.toList()) ;
        setmealDishService.saveBatch(setmealDishes) ;

    }
    /**
     * 删除套餐的同时把关联的菜品也删除
     */
    @Transactional
    @Override
    public void removeWithDish(List<Long> ids) {
        // 只有停售状态的可以删除
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>() ;
        queryWrapper.in(Setmeal::getId,ids) ;
        // 停售的
        queryWrapper.eq(Setmeal::getStatus,1) ;
        // 如果可以删除,先删除套餐表中的数据
        int count = this.count(queryWrapper) ;
        if(count >0 ) {
            // 如果不能删除,抛出一个异常
            throw new CustomException("套餐正在售卖,不能删除") ;
        }

        this.removeByIds(ids) ;

        // 删除关系表中的数据
        LambdaQueryWrapper<SetmealDish> lambdaQueryWrapper = new LambdaQueryWrapper<>() ;
        lambdaQueryWrapper.in(SetmealDish::getSetmealId,ids) ;
        setmealDishService.remove(lambdaQueryWrapper) ;
    }

    /**
     * 修改套餐
     * @param setmealDto
     */
    @Override
    public void updateWithDish(SetmealDto setmealDto) {
        // 修改基本的信息
        // 更新setmeal的信息
        this.updateById(setmealDto);

        //清理当前套餐对应的菜品数据 -- 因为要重新给它赋值

        List<SetmealDish> setmealDtos = setmealDto.getSetmealDishes() ;
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>() ;
        queryWrapper.eq(SetmealDish::getSetmealId,setmealDto.getId()) ;
        setmealDishService.remove(queryWrapper) ;
        // 修改套餐里面的菜品
        // 先删除,再添加
        setmealDtos = setmealDtos.stream().map((item)->{
            // 给每一个菜品注入 套餐的id
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList()) ;
        setmealDishService.saveBatch(setmealDtos) ;

    }
    @Override
    public SetmealDto getByIdWithDish(Long id) {
        // 根据id查询setmeal表中的基本信息
        Setmeal setmeal = this.getById(id);
        SetmealDto setmealDto = new SetmealDto();
        // 对象拷贝。
        BeanUtils.copyProperties(setmeal, setmealDto);
        // 查询关联表setmeal_dish的菜品信息
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId, id);
        List<SetmealDish> setmealDishList = setmealDishService.list(queryWrapper);
        //设置套餐菜品属性
        setmealDto.setSetmealDishes(setmealDishList);
        return setmealDto;
    }
}

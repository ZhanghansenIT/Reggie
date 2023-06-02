package com.it.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.it.reggie.dto.SetmealDto;
import com.it.reggie.entity.Setmeal;
import org.springframework.stereotype.Service;

import java.util.List;


public interface SetmealService extends IService<Setmeal> {
    // 扩展
    /**
     * 新增套餐,同时需要保存套餐和菜品的关联关系
     *
     */
    public void saveWithDish(SetmealDto setmealDto) ;

    /**
     * 删除套餐的同时把关联的菜品也删除
     */
    public void removeWithDish(List<Long> ids) ;

    public void updateWithDish(SetmealDto setmealDto) ;
    // 根据菜品id 查询
    public SetmealDto getByIdWithDish(Long id ) ;

}

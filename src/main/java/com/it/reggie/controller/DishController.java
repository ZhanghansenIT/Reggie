package com.it.reggie.controller;


import com.it.reggie.common.R;
import com.it.reggie.dto.DishDto;
import com.it.reggie.service.DishFlavorService;
import com.it.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/dish")

public class DishController {
    @Autowired
    private DishService dishService ;

    @Autowired
    private DishFlavorService dishFlavorService ;

    /**
     * 新增菜品
     * @return
     */
    @PostMapping()
    public R<String> save(@RequestBody DishDto dishDto){
        log.info(dishDto.toString());

        return R.success("d") ;
    }



}

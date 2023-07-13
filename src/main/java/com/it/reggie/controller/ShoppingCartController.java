package com.it.reggie.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.it.reggie.common.BaseContext;
import com.it.reggie.common.R;
import com.it.reggie.entity.ShoppingCart;
import com.it.reggie.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import sun.rmi.runtime.Log;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/shoppingCart")
@Slf4j
public class ShoppingCartController {

    @Resource
    private ShoppingCartService shoppingCartService ;

    /**
     * 添加购物车
     * @param shoppingCart
     * @return
     */
    @PostMapping("/add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart){

        log.info("添加购物车");
        System.out.println(shoppingCart);

        // 设置用户id ,指定当前是哪个用户的购物车数据
        Long currendId = BaseContext.getCurrentId() ;
        shoppingCart.setUserId(currendId);

        // 查询当前菜品或者套餐是否已经在购物车中了
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>() ;
        queryWrapper.eq(ShoppingCart::getUserId,currendId)  ;

        Long dishId = shoppingCart.getDishId() ;
        if(dishId !=null){
            // 菜品
            queryWrapper.eq(ShoppingCart::getDishId,dishId)  ;
        }else{
            // 套餐
            queryWrapper.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId()) ;
        }

        // 如果已经存在,就在原来基础上加1
        ShoppingCart cartServiceOne =  shoppingCartService.getOne(queryWrapper) ;

        if(cartServiceOne!=null) {
            Integer number = cartServiceOne.getNumber();
            cartServiceOne.setNumber(number+1);
            shoppingCartService.updateById(cartServiceOne) ;
        }else{
            // 不存在添加
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartService.save(shoppingCart) ;
            cartServiceOne = shoppingCart ;
        }


        return R.success(cartServiceOne) ;
    }

    /**
     * 查询购物车
     * @return
     */
    @GetMapping("/list")
    public R<List<ShoppingCart>> list(){
        // 构建查询条件 根据用户ID查询
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,BaseContext.getCurrentId());
        // 根据时间升序
        queryWrapper.orderByAsc(ShoppingCart::getCreateTime);
        List<ShoppingCart> list = shoppingCartService.list(queryWrapper);
        return R.success(list);
    }

    /**
     * 减去
     * @param shoppingCart
     * @return
     */
    @PostMapping("/sub")
    public R<ShoppingCart> sub(@RequestBody ShoppingCart shoppingCart){
        //减

        // 获取正在删除的菜品的id
        Long dishId = shoppingCart.getDishId() ;
        // 获取正在删除的套餐的id
        Long setmealId = shoppingCart.getSetmealId();
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>() ;

        if(dishId !=null) {
            // 通过dishId 查出购物侧的对象
            // 找出该用户所有的购物清单
            queryWrapper.eq(ShoppingCart::getUserId,BaseContext.getCurrentId()) ;
            // 找出要减少的哪个菜品
            queryWrapper.eq(ShoppingCart::getDishId,dishId) ;
//            queryWrapper.eq(ShoppingCart::getUserId,BaseContext.getCurrentId()) ;

            ShoppingCart cart1 = shoppingCartService.getOne(queryWrapper) ;
            Integer number = cart1.getNumber() ;
            cart1.setNumber(number-1);
            if(number -1 >0 ){
                shoppingCartService.updateById(cart1) ;
            }else if(number-1 ==0 ){
                shoppingCartService.removeById(cart1.getId()) ;
            }else{
                return R.error("操作异常") ;
            }
            return R.success(cart1) ;
        }

        // 套餐

        if (setmealId != null){
            //代表是套餐数量减少
            queryWrapper.eq(ShoppingCart::getSetmealId,setmealId).eq(ShoppingCart::getUserId,BaseContext.getCurrentId());
            ShoppingCart cart2 = shoppingCartService.getOne(queryWrapper);
            cart2.setNumber(cart2.getNumber()-1);
            Integer LatestNumber = cart2.getNumber();
            if (LatestNumber > 0){
                //对数据进行更新操作
                shoppingCartService.updateById(cart2);
            }else if(LatestNumber == 0){
                //如果购物车的套餐数量减为0，那么就把套餐从购物车删除
                shoppingCartService.removeById(cart2.getId());
            }else if (LatestNumber < 0){
                return R.error("操作异常");
            }
            return R.success(cart2);
        }
        //如果两个大if判断都进不去
        return R.error("操作异常");



    }
    @DeleteMapping("/clean")
    public R<String> clean(){
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>() ;

        //根据用户id删除
        queryWrapper.eq(ShoppingCart::getUserId,BaseContext.getCurrentId());
        shoppingCartService.remove(queryWrapper)  ;
        return R.success("清空购物车成功" );
    }


}

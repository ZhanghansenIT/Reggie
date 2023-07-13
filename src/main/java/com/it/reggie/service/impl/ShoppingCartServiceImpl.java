package com.it.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;


import com.it.reggie.entity.ShoppingCart;
import com.it.reggie.mapper.ShoppingCartMapper;
import com.it.reggie.service.ShoppingCartService;
import org.springframework.stereotype.Service;

/**
 * @author jektong
 * @date 2022年05月28日 0:22
 */
@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShoppingCartService {
}

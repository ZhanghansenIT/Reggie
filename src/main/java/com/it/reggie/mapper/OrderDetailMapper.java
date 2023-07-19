package com.it.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import com.it.reggie.entity.OrderDetail;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author jektong
 * @date 2022年05月28日 17:16
 */
@Mapper
public interface OrderDetailMapper extends BaseMapper<OrderDetail> {
}

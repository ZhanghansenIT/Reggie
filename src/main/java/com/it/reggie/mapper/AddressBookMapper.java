package com.it.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import com.it.reggie.entity.AddressBook;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author jektong
 * @date 2022年05月25日 20:32
 */
@Mapper
public interface AddressBookMapper extends BaseMapper<AddressBook> {
}

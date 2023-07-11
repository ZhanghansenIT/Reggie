package com.it.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.it.reggie.entity.AddressBook;
import com.it.reggie.mapper.AddressBookMapper;
import com.it.reggie.service.AddressBookService;
import org.springframework.stereotype.Service;

/**
 * @author jektong
 * @date 2022年05月25日 20:35
 */
@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook> implements AddressBookService {
}

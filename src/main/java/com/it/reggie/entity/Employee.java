package com.it.reggie.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class Employee implements Serializable {


    // 序列号
    private static final long serialVersionUID = 1L ;
    // 唯一主键
    private Long id ;

    private String username ;
    private String name ;
    private String password ;

    private String phone ;

    private String sex ;
    private String idNumber ;

    // 状态
    private Integer status ;

    // 创建时间
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime ;

    // 更新时间
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime ;

    // 添加用户时使用
    @TableField(fill = FieldFill.INSERT)
    private Long createUser ;

    // 更新用户时使用
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateUser ;

}

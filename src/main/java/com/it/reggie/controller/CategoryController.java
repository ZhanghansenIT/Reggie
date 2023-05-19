package com.it.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.it.reggie.common.R;
import com.it.reggie.entity.Category;
import com.it.reggie.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 分类管理
 */
@Slf4j
@RestController
@RequestMapping("/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService ;

    /**
     * 新增分类
     * @param category
     * @return
     */
    @PostMapping()
    public R<String> save(@RequestBody Category category){
        log.info("category : {} ",category) ;
        categoryService.save(category) ;
        return R.success("新增分类成功") ;
    }

    /**
     * 返回菜品分页列表信息
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/page")
    public R<Page>  page(int page , int pageSize){
        // 分页构造器
        Page<Category> pageInfo = new Page<>() ;
        // 条件过滤器(排序)
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>() ;
        // 添加排序条件,根据sort进行排序
        queryWrapper.orderByDesc(Category::getSort) ;

        // 进行分页查询
        categoryService.page(pageInfo,queryWrapper) ;
        return R.success(pageInfo) ;
    }

    /**
     * delete category By id
     * 根据id删除菜品
     * @param id
     * @return
     */
    @DeleteMapping
    public R<String> delete(Long id ){
        log.info("删除分类" +id);
//        categoryService.removeById(id) ;

        categoryService.remove(id);

        return R.success("分类信息删除成功") ;
    }

    /**
     * 修改分类菜品信息
     * @return
     */
    @PutMapping()
    public R<String> update(@RequestBody Category category){
        log.info("修改分类信息id : {}" ,category.getId());

        categoryService.updateById(category) ;
        return R.success("修改成功" );
    }
}

package com.yw.secondhandtrade.server.service;

import com.yw.secondhandtrade.pojo.dto.CategoryDTO;
import com.yw.secondhandtrade.pojo.dto.CategoryPageQueryDTO;
import com.yw.secondhandtrade.pojo.entity.Category;
import com.yw.secondhandtrade.common.result.PageResult;

import java.util.List;

public interface CategoryService {

    /**
     * 新增分类
     * @param categoryDTO
     */
    void save(CategoryDTO categoryDTO);

    /**
     * 修改分类
     * @param categoryDTO
     */
    void update(CategoryDTO categoryDTO);

    /**
     * 根据id删除分类
     * @param id
     */
    void deleteById(Long id);

    /**
     * 分页查询
     * @param categoryPageQueryDTO
     * @return
     */
    PageResult pageQuery(CategoryPageQueryDTO categoryPageQueryDTO);

    /**
     * 查询所有分类
     * @return
     */
    List<Category> list();
}

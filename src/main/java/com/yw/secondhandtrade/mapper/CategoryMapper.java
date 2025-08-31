package com.yw.secondhandtrade.mapper;

import com.github.pagehelper.Page;
import com.yw.secondhandtrade.common.annotation.FillTime;
import com.yw.secondhandtrade.common.enumeration.DBOperationType;
import com.yw.secondhandtrade.pojo.dto.CategoryPageQueryDTO;
import com.yw.secondhandtrade.pojo.entity.Category;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface CategoryMapper {

    /**
     * 新增商品分类
     * @param category
     */
    @FillTime(DBOperationType.INSERT)
    void insert(Category category);

    /**
     * 修改商品分类
     * @param category
     */
    @FillTime(DBOperationType.UPDATE)
    void update(Category category);

    /**
     * 根据ID删除商品分类
     * @param id
     */
    void deleteById(Long id);

    /**
     * 分页查询商品分类
     * @param categoryPageQueryDTO
     * @return
     */
    Page<Category> pageQuery(CategoryPageQueryDTO categoryPageQueryDTO);

    /**
     * 查询所有商品分类
     * @return
     */
    List<Category> list();
}

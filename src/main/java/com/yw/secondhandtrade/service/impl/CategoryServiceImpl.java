package com.yw.secondhandtrade.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.yw.secondhandtrade.common.result.PageResult;
import com.yw.secondhandtrade.mapper.CategoryMapper;
import com.yw.secondhandtrade.pojo.dto.CategoryDTO;
import com.yw.secondhandtrade.pojo.dto.CategoryPageQueryDTO;
import com.yw.secondhandtrade.pojo.entity.Category;
import com.yw.secondhandtrade.service.CategoryService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    public void save(CategoryDTO categoryDTO) {
        Category category = new Category();
        BeanUtils.copyProperties(categoryDTO, category);
        categoryMapper.insert(category);
    }

    @Override
    public void update(CategoryDTO categoryDTO) {
        Category category = new Category();
        BeanUtils.copyProperties(categoryDTO, category);
        categoryMapper.update(category);
    }

    @Override
    public void deleteById(Long id) {
        // TODO: 后续可以增加逻辑：检查该分类下是否有关联的商品，如果有则不允许删除
        categoryMapper.deleteById(id);
    }

    @Override
    public PageResult pageQuery(CategoryPageQueryDTO categoryPageQueryDTO) {
        PageHelper.startPage(categoryPageQueryDTO.getPage(), categoryPageQueryDTO.getPageSize());
        Page<Category> page = categoryMapper.pageQuery(categoryPageQueryDTO);
        return new PageResult(page.getTotal(), page.getResult());
    }

    @Override
    public List<Category> list() {
        return categoryMapper.list();
    }
}

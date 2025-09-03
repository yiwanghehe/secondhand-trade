package com.yw.secondhandtrade.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.yw.secondhandtrade.common.constant.CacheConstant;
import com.yw.secondhandtrade.common.result.PageResult;
import com.yw.secondhandtrade.mapper.CategoryMapper;
import com.yw.secondhandtrade.pojo.dto.CategoryDTO;
import com.yw.secondhandtrade.pojo.dto.CategoryPageQueryDTO;
import com.yw.secondhandtrade.pojo.entity.Category;
import com.yw.secondhandtrade.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public void save(CategoryDTO categoryDTO) {
        Category category = new Category();
        BeanUtils.copyProperties(categoryDTO, category);
        categoryMapper.insert(category);

        clearCache();
    }

    @Override
    public void update(CategoryDTO categoryDTO) {
        Category category = new Category();
        BeanUtils.copyProperties(categoryDTO, category);
        categoryMapper.update(category);

        clearCache();
    }

    @Override
    public void deleteById(Long id) {
        // TODO: 后续可以增加逻辑：检查该分类下是否有关联的商品，如果有则不允许删除
        categoryMapper.deleteById(id);

        clearCache();
    }

    @Override
    public PageResult pageQuery(CategoryPageQueryDTO categoryPageQueryDTO) {
        PageHelper.startPage(categoryPageQueryDTO.getPage(), categoryPageQueryDTO.getPageSize());
        Page<Category> page = categoryMapper.pageQuery(categoryPageQueryDTO);
        return new PageResult(page.getTotal(), page.getResult());
    }

    @Override
    public List<Category> list() {
        // 从Redis中查询缓存
        Object categoryCacheObj = redisTemplate.opsForValue().get(CacheConstant.CATEGORY_CACHE_KEY);

        // 类型检查
        if (categoryCacheObj instanceof List) {
            @SuppressWarnings("unchecked")
            List<Category> list = (List<Category>) categoryCacheObj;
            if (!list.isEmpty()) {
                log.info("命中商品分类缓存");
                return list;
            }
        }

        // 如果缓存未命中，查询数据库
        log.info("未命中分类缓存，查询数据库");
        List<Category> list = categoryMapper.list();

        // 将查询结果存入Redis
        redisTemplate.opsForValue().set(CacheConstant.CATEGORY_CACHE_KEY, list);

        return list;
    }

    /**
     * 清理商品分类缓存
     */
    private void clearCache(){
        log.info("清理商品分类缓存: {}", CacheConstant.CATEGORY_CACHE_KEY);
        redisTemplate.delete(CacheConstant.CATEGORY_CACHE_KEY);
    }
}

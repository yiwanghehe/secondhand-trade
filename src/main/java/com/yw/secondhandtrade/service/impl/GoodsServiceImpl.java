package com.yw.secondhandtrade.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.yw.secondhandtrade.common.constant.CacheConstant;
import com.yw.secondhandtrade.common.constant.MessageConstant;
import com.yw.secondhandtrade.common.constant.StatusConstant;
import com.yw.secondhandtrade.common.context.BaseContext;
import com.yw.secondhandtrade.common.exception.BusinessException;
import com.yw.secondhandtrade.common.exception.GoodsNotFoundException;
import com.yw.secondhandtrade.common.result.PageResult;
import com.yw.secondhandtrade.mapper.GoodsMapper;
import com.yw.secondhandtrade.pojo.dto.GoodsDTO;
import com.yw.secondhandtrade.pojo.dto.GoodsPageQueryDTO;
import com.yw.secondhandtrade.pojo.entity.Goods;
import com.yw.secondhandtrade.service.GoodsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class GoodsServiceImpl implements GoodsService {

    @Autowired
    private GoodsMapper goodsMapper;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    // --- 管理端方法实现 ---

    /**
     * 【管理端】新增商品
     * @param goodsDTO
     */
    @Override
    @Transactional
    public void save(GoodsDTO goodsDTO) {
        Goods goods = new Goods();
        BeanUtils.copyProperties(goodsDTO, goods);

        // 从线程上下文获取当前登录用户的ID，并设置为卖家ID
        Long currentUserId = BaseContext.getId();
        goods.setSellerId(currentUserId);

        // 新发布的商品默认在售
        goods.setStatus(StatusConstant.ENABLE);

        if(goods.getDescription() == null){
            goods.setDescription(MessageConstant.GOODS_TEST_DESCRIPTION);
        }
        // 二手商品库存默认为1
        if (goods.getStock() == null) {
            goods.setStock(1);
        }

        goodsMapper.insert(goods);

        clearCache();
    }

    /**
     * 【通用】根据id查询商品
     * @param id
     * @return
     */
    @Override
    public Goods getById(Long id) {
        Goods goods = goodsMapper.getById(id);
        if(goods == null) {
            throw new GoodsNotFoundException(MessageConstant.GOODS_NOT_FOUND_OR_NO_PERMISSION);
        }
        return goods;
    }

    /**
     * 【管理端】修改商品
     * @param goodsDTO
     */
    @Override
    public void update(GoodsDTO goodsDTO) {
        Goods goods = new Goods();
        BeanUtils.copyProperties(goodsDTO, goods);
        goodsMapper.update(goods);

        clearCache();
    }

    /**
     * 【管理端】商品上下架
     * @param status
     * @param id
     */
    @Override
    public void startOrStop(Integer status, Long id) {
        Goods goods = Goods.builder()
                .id(id)
                .status(status)
                .build();
        goodsMapper.update(goods);

        clearCache();
    }

    /**
     * 【管理端】根据id删除商品
     * @param id
     */
    @Override
    public void deleteById(Long id) {
        goodsMapper.deleteById(id);

        clearCache();
    }

    // --- 用户端方法实现 ---

    /**
     * 【用户端】发布商品
     * @param goodsDTO
     */
    @Override
    @Transactional
    public void publish(GoodsDTO goodsDTO) {
        Goods goods = new Goods();
        BeanUtils.copyProperties(goodsDTO, goods);

        // 从线程上下文获取当前登录用户的ID，并设置为卖家ID
        Long currentUserId = BaseContext.getId();
        goods.setSellerId(currentUserId);

        // 新发布的商品默认在售
        goods.setStatus(StatusConstant.ENABLE);
        // 二手商品库存默认为1
        if (goods.getStock() == null) {
            goods.setStock(1);
        }

        goodsMapper.insert(goods);

        clearCache();
    }

    /**
     * 【用户端】查询我发布的商品
     * @return
     */
    @Override
    public List<Goods> getMyPublished() {
        // 获取当前登录用户的ID
        Long currentUserId = BaseContext.getId();
        List<Goods> goodsList = goodsMapper.getBySellerId(currentUserId);

        return goodsList;
    }

    /**
     * 【用户端】更改我的商品状态（下架、重新上架、售出等）
     * @param id 商品ID
     * @param status 要设置的状态
     */
    @Override
    public void changeMyGoodsStatus(Long id, Integer status) {
        // 获取当前登录用户的ID
        Long currentUserId = BaseContext.getId();

        // 权限校验：查询数据库，确认该商品是否属于当前用户
        Goods goodsInDb = goodsMapper.getById(id);

        if (goodsInDb == null || !goodsInDb.getSellerId().equals(currentUserId)) {
            // 如果商品不存在，或者商品的卖家ID与当前用户ID不匹配，则抛出异常
            throw new BusinessException(MessageConstant.GOODS_NOT_FOUND_OR_NO_PERMISSION);
        }

        // 验证状态参数是否有效
        if (!StatusConstant.isValidStatus(status)) {
            throw new BusinessException(MessageConstant.GOODS_INVALID_STATUS);
        }

        // 构造更新对象，执行状态变更操作
        Goods goodsToUpdate = Goods.builder()
                .id(id)
                .status(status)
                .build();

        goodsMapper.update(goodsToUpdate);

        clearCache();
    }

    /**
     * 【用户端】修改我的商品信息
     * @param goodsDTO
     */
    @Override
    public void updateMyGoods(GoodsDTO goodsDTO) {
        // 获取当前登录用户的ID
        Long currentUserId = BaseContext.getId();

        // 权限校验
        Goods goodsInDb = goodsMapper.getById(goodsDTO.getId());
        if (goodsDTO.getId() == null || goodsInDb == null || !goodsInDb.getSellerId().equals(currentUserId)) {
            throw new BusinessException(MessageConstant.GOODS_NOT_FOUND_OR_NO_PERMISSION);
        }

        // 执行更新
        Goods goodsToUpdate = new Goods();
        BeanUtils.copyProperties(goodsDTO, goodsToUpdate);
        goodsMapper.update(goodsToUpdate);

        clearCache();
    }

    /**
     * 【公共】商品分页查询
     * @param goodsPageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQueryPublic(GoodsPageQueryDTO goodsPageQueryDTO) {
        // 根据查询参数动态生成缓存的key
        String cacheKey = CacheConstant.GOODS_CACHE_KEY +
                "::" + goodsPageQueryDTO.getCategoryId() +
                "::" + (goodsPageQueryDTO.getName() == null ? "null" : goodsPageQueryDTO.getName()) +
                "::" + goodsPageQueryDTO.getPage() +
                "::" + goodsPageQueryDTO.getPageSize();

        // 从Redis缓存中查询数据
        Object goodsCacheObj = redisTemplate.opsForValue().get(cacheKey);

        // 判断缓存是否命中
        if (goodsCacheObj instanceof PageResult) {
            // 类型检查通过，说明是有效的缓存数据
            log.info("命中商品分页缓存: {}", cacheKey);
            return (PageResult) goodsCacheObj;
        }

        // 如果缓存未命中，则查询数据库
        log.info("未命中商品分页缓存，查询数据库: {}", cacheKey);
        PageHelper.startPage(goodsPageQueryDTO.getPage(), goodsPageQueryDTO.getPageSize());
        Page<Goods> page = goodsMapper.pageQueryPublic(goodsPageQueryDTO);
        PageResult pageResult = new PageResult(page.getTotal(), page.getResult());

        // 将查询结果存入Redis，并设置过期时间（1小时）
        redisTemplate.opsForValue().set(cacheKey, pageResult, 1, TimeUnit.HOURS);

        return pageResult;
    }

    /**
     * 清理商品分页缓存
     */
    private void clearCache() {
        // 构造匹配模式，* 是通配符
        String cacheKeyPattern = CacheConstant.GOODS_CACHE_KEY + "*";
        log.info("准备清理商品分页缓存，匹配模式: {}", cacheKeyPattern);

        // 查找所有匹配模式的key
        Set<String> keys = redisTemplate.keys(cacheKeyPattern);

        // 判断key集合不为空，然后执行删除
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
            log.info("成功清理了 {} 条商品缓存", keys.size());
        } else {
            log.info("没有找到匹配的商品缓存需要清理");
        }
    }

}

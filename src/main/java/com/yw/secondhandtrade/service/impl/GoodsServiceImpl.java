package com.yw.secondhandtrade.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.yw.secondhandtrade.common.constant.MessageConstant;
import com.yw.secondhandtrade.common.constant.StatusConstant;
import com.yw.secondhandtrade.common.context.BaseContext;
import com.yw.secondhandtrade.common.exception.BusinessException;
import com.yw.secondhandtrade.common.result.PageResult;
import com.yw.secondhandtrade.mapper.GoodsMapper;
import com.yw.secondhandtrade.pojo.dto.GoodsDTO;
//import com.yw.secondhandtrade.pojo.dto.GoodsPageQueryDTO;
import com.yw.secondhandtrade.pojo.entity.Goods;
import com.yw.secondhandtrade.service.GoodsService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class GoodsServiceImpl implements GoodsService {

    @Autowired
    private GoodsMapper goodsMapper;

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
        // 默认状态为“在售”
        goods.setStatus(StatusConstant.ENABLE);
        goodsMapper.insert(goods);
    }

    /**
     * 【通用】根据id查询商品
     * @param id
     * @return
     */
    @Override
    public Goods getById(Long id) {
        return goodsMapper.getById(id);
    }

//    /**
//     * 【管理端】商品分页查询
//     * @param goodsPageQueryDTO
//     * @return
//     */
//    @Override
//    public PageResult pageQuery(GoodsPageQueryDTO goodsPageQueryDTO) {
//        PageHelper.startPage(goodsPageQueryDTO.getPage(), goodsPageQueryDTO.getPageSize());
//        Page<Goods> page = goodsMapper.pageQuery(goodsPageQueryDTO);
//        return new PageResult(page.getTotal(), page.getResult());
//    }

    /**
     * 【管理端】修改商品
     * @param goodsDTO
     */
    @Override
    public void update(GoodsDTO goodsDTO) {
        Goods goods = new Goods();
        BeanUtils.copyProperties(goodsDTO, goods);
        goodsMapper.update(goods);
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
    }

    /**
     * 【管理端】根据id删除商品
     * @param id
     */
    @Override
    public void deleteById(Long id) {
        goodsMapper.deleteById(id);
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

        // 关键：从线程上下文获取当前登录用户的ID，并设置为卖家ID
        Long currentUserId = BaseContext.getId();
        goods.setSellerId(currentUserId);

        // 新发布的商品默认在售
        goods.setStatus(StatusConstant.ENABLE);
        // 二手商品库存默认为1
        if (goods.getStock() == null) {
            goods.setStock(1);
        }

        goodsMapper.insert(goods);
    }

    /**
     * 【用户端】查询我发布的商品
     * @return
     */
    @Override
    public List<Goods> getMyPublished() {
        // 1. 获取当前登录用户的ID
        Long currentUserId = BaseContext.getId();
        System.out.println(currentUserId);
        // 2. 调用Mapper方法，根据用户ID查询商品列表
        List<Goods> goodsList = goodsMapper.getBySellerId(currentUserId);

        return goodsList;
    }

    /**
     * 【用户端】下架我的商品
     * @param id
     */
    @Override
    public void takedownMyGoods(Long id) {
        // 1. 获取当前登录用户的ID
        Long currentUserId = BaseContext.getId();

        // 2. 权限校验：查询数据库，确认该商品是否属于当前用户
        Goods goodsInDb = goodsMapper.getById(id);

        if (goodsInDb == null || !goodsInDb.getSellerId().equals(currentUserId)) {
            // 如果商品不存在，或者商品的卖家ID与当前用户ID不匹配，则抛出异常
            throw new BusinessException(MessageConstant.GOODS_NOT_FOUND_OR_NO_PERMISSION);
        }

        // 3. 构造更新对象，执行下架操作
        Goods goodsToUpdate = Goods.builder()
                .id(id)
                .status(StatusConstant.DISABLE) // 2: 代表下架
                .build();

        goodsMapper.update(goodsToUpdate);
    }

    /**
     * 【用户端】修改我的商品信息
     * @param goodsDTO
     */
    @Override
    public void updateMyGoods(GoodsDTO goodsDTO) {
        // 1. 获取当前登录用户的ID
        Long currentUserId = BaseContext.getId();

        // 2. 权限校验
        Goods goodsInDb = goodsMapper.getById(goodsDTO.getId());
        if (goodsInDb == null || !goodsInDb.getSellerId().equals(currentUserId)) {
            throw new BusinessException(MessageConstant.GOODS_NOT_FOUND_OR_NO_PERMISSION);
        }

        // 3. 执行更新
        Goods goodsToUpdate = new Goods();
        BeanUtils.copyProperties(goodsDTO, goodsToUpdate);
        goodsMapper.update(goodsToUpdate);
    }
}

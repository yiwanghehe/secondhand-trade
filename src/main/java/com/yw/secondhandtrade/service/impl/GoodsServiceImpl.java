package com.yw.secondhandtrade.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.yw.secondhandtrade.common.constant.MessageConstant;
import com.yw.secondhandtrade.common.constant.StatusConstant;
import com.yw.secondhandtrade.common.context.BaseContext;
import com.yw.secondhandtrade.common.exception.BusinessException;
import com.yw.secondhandtrade.common.exception.GoodsNotFoundException;
import com.yw.secondhandtrade.common.result.PageResult;
import com.yw.secondhandtrade.mapper.GoodsMapper;
import com.yw.secondhandtrade.pojo.dto.GoodsDTO;
//import com.yw.secondhandtrade.pojo.dto.GoodsPageQueryDTO;
import com.yw.secondhandtrade.pojo.dto.GoodsPageQueryDTO;
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
    }

    /**
     * 【公共】商品分页查询
     * @param goodsPageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQueryPublic(GoodsPageQueryDTO goodsPageQueryDTO) {
        PageHelper.startPage(goodsPageQueryDTO.getPage(), goodsPageQueryDTO.getPageSize());
        Page<Goods> page = goodsMapper.pageQueryPublic(goodsPageQueryDTO);
        return new PageResult(page.getTotal(), page.getResult());
    }


}

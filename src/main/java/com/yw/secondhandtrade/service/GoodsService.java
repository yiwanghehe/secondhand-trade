package com.yw.secondhandtrade.service;

import com.yw.secondhandtrade.common.result.PageResult;
import com.yw.secondhandtrade.pojo.dto.GoodsDTO;
//import com.yw.secondhandtrade.pojo.dto.GoodsPageQueryDTO;
import com.yw.secondhandtrade.pojo.entity.Goods;

import java.util.List;

public interface GoodsService {

    // --- 管理端方法 ---

    /**
     * 【管理端】新增商品
     * @param goodsDTO
     */
    void save(GoodsDTO goodsDTO);

    /**
     * 【通用】根据id查询商品
     * @param id
     * @return
     */
    Goods getById(Long id);

//    /**
//     * 【管理端】商品分页查询
//     * @param goodsPageQueryDTO
//     * @return
//     */
//    PageResult pageQuery(GoodsPageQueryDTO goodsPageQueryDTO);

    /**
     * 【管理端】修改商品
     * @param goodsDTO
     */
    void update(GoodsDTO goodsDTO);

    /**
     * 【管理端】商品上下架
     * @param status
     * @param id
     */
    void startOrStop(Integer status, Long id);

    /**
     * 【管理端】根据id删除商品
     * @param id
     */
    void deleteById(Long id);

    // --- 用户端方法 ---

    /**
     * 【用户端】发布商品
     * @param goodsDTO
     */
    void publish(GoodsDTO goodsDTO);

    /**
     * 【用户端】查询我发布的商品
     * @return
     */
    List<Goods> getMyPublished();

    /**
     * 【用户端】更改我的商品状态（下架、重新上架、售出等）
     * @param id 商品ID
     * @param status 要设置的状态
     */
    void changeMyGoodsStatus(Long id, Integer status);

    /**
     * 【用户端】修改我的商品信息
     * @param goodsDTO
     */
    void updateMyGoods(GoodsDTO goodsDTO);
}

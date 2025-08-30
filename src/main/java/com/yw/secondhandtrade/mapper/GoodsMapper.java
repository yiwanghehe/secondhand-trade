package com.yw.secondhandtrade.mapper;

import com.github.pagehelper.Page;
import com.yw.secondhandtrade.common.annotation.FillTime;
import com.yw.secondhandtrade.common.enumeration.DBOperationType;
//import com.yw.secondhandtrade.pojo.dto.GoodsPageQueryDTO;
import com.yw.secondhandtrade.pojo.entity.Goods;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface GoodsMapper {

    /**
     * 插入商品数据
     * @param goods
     */
    @FillTime(DBOperationType.INSERT)
    void insert(Goods goods);

    /**
     * 根据id查询商品信息
     * @param id
     * @return
     */
    Goods getById(Long id);

//    /**
//     * 【管理端】动态条件分页查询
//     * @param goodsPageQueryDTO
//     * @return
//     */
//    Page<Goods> pageQuery(GoodsPageQueryDTO goodsPageQueryDTO);

    /**
     * 根据商品信息动态修改
     * @param goods
     */
    @FillTime(DBOperationType.UPDATE)
    void update(Goods goods);

    /**
     * 【管理端】根据id删除商品
     * @param id
     */
    void deleteById(Long id);

    /**
     * 【用户端】根据卖家ID查询其发布的商品列表
     * @param sellerId
     * @return
     */
    List<Goods> getBySellerId(Long sellerId);
}

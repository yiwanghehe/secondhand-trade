package com.yw.secondhandtrade.server.mapper;

import com.yw.secondhandtrade.pojo.entity.Favorites;
import com.yw.secondhandtrade.pojo.entity.Goods;
import com.yw.secondhandtrade.common.enumeration.DBOperationType;
import com.yw.secondhandtrade.server.annotation.FillTime;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface FavoritesMapper {

    /**
     * 新增收藏
     * @param favorites
     */
    @FillTime(DBOperationType.INSERT)
    void insert(Favorites favorites);

    /**
     * 根据用户ID和商品ID删除收藏
     * @param userId
     * @param goodsId
     */
    void deleteByUserIdAndGoodsId(@Param("userId") Long userId, @Param("goodsId") Long goodsId);

    /**
     * 根据用户ID和商品ID查询收藏记录
     * @param userId
     * @param goodsId
     * @return
     */
    Favorites getByUserIdAndGoodsId(@Param("userId") Long userId, @Param("goodsId") Long goodsId);

    /**
     * 根据用户ID查询其收藏的所有商品信息
     * @param userId
     * @return
     */
    List<Goods> listFavoritesByUserId(Long userId);
}

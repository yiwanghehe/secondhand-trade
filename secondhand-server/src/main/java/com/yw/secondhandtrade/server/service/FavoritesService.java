package com.yw.secondhandtrade.server.service;

import com.yw.secondhandtrade.pojo.dto.FavoritesDTO;
import com.yw.secondhandtrade.pojo.entity.Goods;
import java.util.List;

public interface FavoritesService {

    /**
     * 添加收藏
     * @param favoritesDTO
     */
    void add(FavoritesDTO favoritesDTO);

    /**
     * 取消收藏
     * @param favoritesDTO
     */
    void remove(FavoritesDTO favoritesDTO);

    /**
     * 获取当前用户的收藏列表
     * @return
     */
    List<Goods> list();
}

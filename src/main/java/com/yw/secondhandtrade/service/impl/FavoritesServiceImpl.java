package com.yw.secondhandtrade.service.impl;

import com.yw.secondhandtrade.common.context.BaseContext;
import com.yw.secondhandtrade.mapper.FavoritesMapper;
import com.yw.secondhandtrade.pojo.dto.FavoritesDTO;
import com.yw.secondhandtrade.pojo.entity.Favorites;
import com.yw.secondhandtrade.pojo.entity.Goods;
import com.yw.secondhandtrade.service.FavoritesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class FavoritesServiceImpl implements FavoritesService {

    @Autowired
    private FavoritesMapper favoritesMapper;

    @Override
    public void add(FavoritesDTO favoritesDTO) {
        Long userId = BaseContext.getId();
        Long goodsId = favoritesDTO.getGoodsId();

        // 检查是否已收藏，防止重复插入
        Favorites existingFavorite = favoritesMapper.getByUserIdAndGoodsId(userId, goodsId);
        if (existingFavorite == null) {
            Favorites favorites = Favorites.builder()
                    .userId(userId)
                    .goodsId(goodsId)
                    .build();
            favoritesMapper.insert(favorites);
        }
    }

    @Override
    public void remove(FavoritesDTO favoritesDTO) {
        Long userId = BaseContext.getId();
        Long goodsId = favoritesDTO.getGoodsId();
        favoritesMapper.deleteByUserIdAndGoodsId(userId, goodsId);
    }

    @Override
    public List<Goods> list() {
        Long userId = BaseContext.getId();
        return favoritesMapper.listFavoritesByUserId(userId);
    }
}

package com.yw.secondhandtrade.service;

import com.yw.secondhandtrade.pojo.dto.RatingDTO;

public interface RatingService {

    /**
     * 提交评价
     * @param ratingDTO
     */
    void submit(RatingDTO ratingDTO);
}

package com.yw.secondhandtrade.common.constant;

/**
 * 状态常量
 */
public class StatusConstant {

    //启用、在售
    public static final Integer ENABLE = 0;

    //禁用、售出
    public static final Integer DISABLE = 1;

    //下架
    public static final Integer OFF_SHELF = 2;

    /**
     * 验证状态值是否有效
     * @param status 状态值
     * @return 是否有效
     */
    public static boolean isValidStatus(Integer status) {
        return StatusConstant.ENABLE.equals(status) ||
                StatusConstant.DISABLE.equals(status) ||
                StatusConstant.OFF_SHELF.equals(status);
    }
}


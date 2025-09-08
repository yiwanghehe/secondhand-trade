package com.yw.secondhandtrade.common.constant;

/**
 * 状态常量
 */
public class StatusConstant {

    // 通用状态
    //启用、在售
    public static final Integer ENABLE = 0;
    //禁用、售出
    public static final Integer DISABLE = 1;
    //下架
    public static final Integer OFF_SHELF = 2;

    // 地址默认状态
    // 默认地址
    public static final Integer DEFAULT_ADDRESS = 0;
    // 非默认地址
    public static final Integer NON_DEFAULT_ADDRESS = 1;

    // 聊天消息状态
    // 未读
    public static final Integer UNREAD = 0;
    // 已读
    public static final Integer READ = 1;

    /**
     * 验证通用状态值是否有效
     * @param status 状态值
     * @return 是否有效
     */
    public static boolean isValidStatus(Integer status) {
        return StatusConstant.ENABLE.equals(status) ||
                StatusConstant.DISABLE.equals(status) ||
                StatusConstant.OFF_SHELF.equals(status);
    }
}

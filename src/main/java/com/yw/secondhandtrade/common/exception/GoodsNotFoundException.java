package com.yw.secondhandtrade.common.exception;

/**
 * 账号不存在异常
 */
public class GoodsNotFoundException extends BaseException {

    public GoodsNotFoundException() {
    }

    public GoodsNotFoundException(String msg) {
        super(msg);
    }

}

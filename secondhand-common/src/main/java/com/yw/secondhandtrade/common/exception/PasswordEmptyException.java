package com.yw.secondhandtrade.common.exception;

/**
 * 密码不能为空异常
 */
public class PasswordEmptyException extends BaseException{
    public PasswordEmptyException() {
    }

    public PasswordEmptyException(String msg) {
        super(msg);
    }

}

package com.yw.secondhandtrade.common.annotation;


import com.yw.secondhandtrade.common.enumeration.DBOperationType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface FillTime {
    DBOperationType value();
}

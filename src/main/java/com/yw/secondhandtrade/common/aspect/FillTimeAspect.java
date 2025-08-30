package com.yw.secondhandtrade.common.aspect;


import com.yw.secondhandtrade.common.annotation.FillTime;
import com.yw.secondhandtrade.common.constant.FillTimeConstant;
import com.yw.secondhandtrade.common.enumeration.DBOperationType;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

@Aspect
@Component
public class FillTimeAspect {

    /**
     * 切点
     * mapper包下包含FillTime注解的所有类和方法
     */
    @Pointcut("execution(* com.yw.secondhandtrade.mapper.*.*(..)) && @annotation(com.yw.secondhandtrade.common.annotation.FillTime)")
    public void fillTimePointCut(){

    }

    /**
     * 前置通知，在通知中进行公共字段的赋值
     */
    @Before("fillTimePointCut()")
    public void FillTime(JoinPoint joinPoint) {

//        获取到当前拦截的方法上的数据库操作类型
//        获取方法签名对象
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();

//        获取方法上的注解对象
        FillTime fillTime = signature.getMethod().getAnnotation(FillTime.class);

//        获取数据库操作类型
        DBOperationType dbOperationType = fillTime.value();

//        获取到当前被拦截的方法的参数---实体对象
        Object[] args = joinPoint.getArgs();
        if (args == null || args.length == 0) {
            return;
        }

        Object entity = args[0];

//        转变赋值的数据
        LocalDateTime now = LocalDateTime.now();

//        根据当前不同的操作类型，为对应的属性通过反射来赋值
        if (dbOperationType == DBOperationType.INSERT) {
//            为4个公共字段赋值
            try {
                Method setCreateTime = entity.getClass().getDeclaredMethod(FillTimeConstant.SET_CREATE_TIME, LocalDateTime.class);
                Method setUpdateTime = entity.getClass().getDeclaredMethod(FillTimeConstant.SET_UPDATE_TIME, LocalDateTime.class);

//              通过反射为对象赋值
                setCreateTime.invoke(entity, now);
                setUpdateTime.invoke(entity, now);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        } else if (dbOperationType == DBOperationType.UPDATE) {
//            为2个公共字段赋值
            try {
                Method setUpdateTime = entity.getClass().getDeclaredMethod(FillTimeConstant.SET_UPDATE_TIME, LocalDateTime.class);

//              通过反射为对象赋值
                setUpdateTime.invoke(entity, now);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }


    }
}

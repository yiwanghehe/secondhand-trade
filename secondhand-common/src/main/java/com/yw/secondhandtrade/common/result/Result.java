package com.yw.secondhandtrade.common.result;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.io.Serializable;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result<T> implements Serializable {

    private Integer code;
    private String msg;
    private T data;

    public static <T> Result<T> success(){
        Result<T> result = new Result<>();
        result.code = HttpStatus.OK.value();
        return result;
    }
    public static <T> Result<T> success(T data){
        Result<T> result = new Result<>();
        result.code = HttpStatus.OK.value();
        result.data = data;
        return result;
    }
    public static <T> Result<T> success(String msg, T data){
        Result<T> result = new Result<>();
        result.code = HttpStatus.OK.value();
        result.msg = msg;
        result.data = data;
        return result;
    }
    public static <T> Result<T> success(Integer code, String msg, T data){
        Result<T> result = new Result<>();
        result.code = code;
        result.msg = msg;
        result.data = data;
        return result;
    }

    public static <T> Result<T> error(Integer code){
        Result<T> result = new Result<>();
        result.code = code;
        result.msg = code + "error";
        return result;
    }
    public static <T> Result<T> error(String msg){
        Result<T> result = new Result<>();
        result.code = HttpStatus.BAD_REQUEST.value();
        result.msg = msg;
        return result;
    }
    public static <T> Result<T> error(Integer code, String msg){
        Result<T> result = new Result<>();
        result.code = code;
        result.msg = msg;
        return result;
    }



}

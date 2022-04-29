package com.sunzy.reggie.common;

/**
 * 自定业务异常类
 */
public class CustomException extends RuntimeException {
    public CustomException(String message){
        super(message);
    }
}

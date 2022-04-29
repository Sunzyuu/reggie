package com.sunzy.reggie.common;

import org.springframework.stereotype.Component;

/**
 * 基于ThreadLocal实现保存当前登录用户id
 */
//@Component
public class BaseContext {
    private static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    /**
     * 设置值
     * @param id
     */
    public static void setCurrentId(Long id){
        threadLocal.set(id);
    }

    /**
     * 获取值
     * @return
     */
    public static Long getCurrentId(){
        return threadLocal.get();
    }
}

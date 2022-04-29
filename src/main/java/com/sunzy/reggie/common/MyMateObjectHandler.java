package com.sunzy.reggie.common;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;
import com.sunzy.reggie.common.BaseContext;

import java.time.LocalDateTime;


/**
 * 自定义元数据处理器
 */
@Component
@Slf4j
public class MyMateObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        log.info("公共字段自动填充insert");
        metaObject.setValue("createTime", LocalDateTime.now());
        metaObject.setValue("updateTime", LocalDateTime.now());
        log.info("basecontext id:{}", BaseContext.getCurrentId());
        metaObject.setValue("createUser", BaseContext.getCurrentId());
//        metaObject.setValue("createUser", new Long(1));
        metaObject.setValue("updateUser", BaseContext.getCurrentId());
//        metaObject.setValue("updateUser", new Long(1));
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        long id = Thread.currentThread().getId();
        log.info("当前线程id: {}", id);
        metaObject.setValue("updateTime", LocalDateTime.now());
        metaObject.setValue("updateUser", BaseContext.getCurrentId());
//        metaObject.setValue("updateUser", new Long(1));
    }
}

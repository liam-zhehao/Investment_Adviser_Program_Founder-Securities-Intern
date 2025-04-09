package com.foundersc.ifte.invest.adviser.web.config.support;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Date;

/**
 * 用于自动处理mybatis插入数据库时的字段赋值
 */
@Component("mybatisColumnsHandler")
@Slf4j
public class MybatisColumnsHandler implements MetaObjectHandler {
    @PostConstruct
    public void init() {
        log.info("init mybatisColumnsHandler");
    }

    /**
     * 设置数据新增时候的，字段自动赋值规则
     *
     * @param metaObject
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        this.strictInsertFill(metaObject, "createDate", Date.class, new Date());
        this.strictInsertFill(metaObject, "updateDate", Date.class, new Date());
        // createdBy, updatedBy
    }

    /**
     * 设置数据修改update时候的，字段自动赋值规则
     *
     * @param metaObject
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        this.strictInsertFill(metaObject, "updateDate", Date.class, new Date());
        // updatedBy
    }
}

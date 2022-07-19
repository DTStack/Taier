package com.dtstack.taier.dao.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;

import java.sql.Timestamp;
import java.time.Instant;

/**
 * @author yuebai
 * @date 2022/7/19
 */
public class TableEntityHandler implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject) {
        this.strictInsertFill(metaObject, "gmtCreate", Timestamp.class, Timestamp.from(Instant.now()));
        this.strictInsertFill(metaObject, "gmtModified", Timestamp.class, Timestamp.from(Instant.now()));
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.strictUpdateFill(metaObject, "gmtModified", Timestamp.class, Timestamp.from(Instant.now()));
    }
}

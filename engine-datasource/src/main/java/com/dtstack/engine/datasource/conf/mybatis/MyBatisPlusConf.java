package com.dtstack.engine.datasource.conf.mybatis;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.dtstack.engine.datasource.auth.MetaObjectHolder;
import com.dtstack.engine.datasource.dao.BaseMapperField;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.Date;

/**
 * MP配置文件类
 * @description:
 * @author: liuxx
 * @date: 2021/3/15
 */
@Configuration
public class MyBatisPlusConf {

    @Primary
    @Bean
    public MetaObjectHandler metaObjectHandler() {
        return new MetaObjectHandler() {
            @Override
            public void insertFill(MetaObject metaObject) {
                this.strictInsertFill(metaObject, BaseMapperField.FIELD_CREATE_AT, Date.class, new Date());
                this.strictInsertFill(metaObject, BaseMapperField.FIELD_CREATE_BY, Long.class, MetaObjectHolder.uid());
            }

            @Override
            public void updateFill(MetaObject metaObject) {
                this.strictUpdateFill(metaObject, BaseMapperField.FIELD_UPDATE_AT, Date.class, new Date());
                this.strictUpdateFill(metaObject, BaseMapperField.FIELD_UPDATE_BY, Long.class, MetaObjectHolder.uid());
            }
        };
    }

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }
}

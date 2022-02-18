package com.dtstack.taier.develop.service.datasource.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dtstack.taier.dao.mapper.IMapper;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Function;

/**
 * SLOGAN:让现在编程未来
 *
 * @author <a href="mailto:linfeng@dtstack.com">林丰</a> 2020/10/22.
 */
public abstract class BaseService<M extends IMapper<T>, T> extends ServiceImpl<M, T> {

    /**
     * Object -> Integer
     */
    private final static Function<Object, Integer> GET_INTEGER = obj -> Optional.ofNullable(obj).map(x -> Integer.valueOf(x.toString())).orElse(null);
    /**
     * Object -> Long
     */
    private final static Function<Object, Long> GET_LONG = obj -> Optional.ofNullable(obj).map(x -> Long.valueOf(x.toString())).orElse(null);
    /**
     * Object -> String
     */
    private final static Function<Object, String> GET_STRING = obj -> Optional.ofNullable(obj).map(Object::toString).orElse(null);

    /**
     * 查询一行记录, (存在多行记录则返回第一行, 不抛异常)
     *
     * @param qw 查询条件封装
     */
    public T selectOne(QueryWrapper<T> qw) {
        return super.getOne(qw.last("limit 1"), false);
    }

    /**
     * 批量插入（参数为null或empty则返回false）
     *
     * @param entities  实体集合
     * @param batchSize 分批插入大小
     * @return true操作成功 false操作失败
     */
    public boolean insertBatch(Collection<T> entities, int batchSize) {
        if (CollectionUtils.isEmpty(entities)) {
            return false;
        }
        return super.saveBatch(entities, batchSize);
    }

    /**
     * 批量插入（参数为null或empty则返回false）
     * 默认batchSize为1000
     *
     * @param entities 实体集合
     * @return true操作成功 false操作失败
     */
    public boolean insertBatch(Collection<T> entities) {
        return insertBatch(entities, 1000);
    }

    /**
     * 查询一行记录, (根据throwEx判断是否抛异常, 存在多行记录则抛异常)
     *
     * @param qw 查询条件封装
     */
    public T selectOne(QueryWrapper<T> qw, boolean throwEx) {
        return super.getOne(qw, throwEx);
    }

    /**
     * 查询记录是否存在
     * (封装了 select 1 ... limit 1)
     *
     * @return 存在=>true  不存在=>false
     */
    public boolean existBy(QueryWrapper<T> qw) {
        return super.getObj(qw.select("1").last("limit 1"), GET_INTEGER) != null;
    }

    /**
     * 查询一行记录（limit 1）中的某个char/varchar/nvarchar字段
     *
     * @param qw 请传入.eq() .select()
     * @return 某个varchar字段的值
     */
    public String selectObjString(QueryWrapper<T> qw) {
        return super.getObj(qw.last("limit 1"), GET_STRING);
    }

    /**
     * 同上（某个int字段）
     */
    public Integer selectObjInteger(QueryWrapper<T> qw) {
        return super.getObj(qw.last("limit 1"), GET_INTEGER);
    }

    /**
     * 同上（某个bigint字段）
     */
    public Long selectObjLong(QueryWrapper<T> qw) {
        return super.getObj(qw.last("limit 1"), GET_LONG);
    }


    /**
     * 返回空的Wrapper
     */
    protected QueryWrapper<T> qw() {
        return Wrappers.query();
    }

    /**
     * 返回空的Wrapper
     */
    protected UpdateWrapper<T> uw() {
        return Wrappers.update();
    }


}

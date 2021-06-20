package com.dtstack.engine.datasource.common.utils;

import com.google.common.collect.Lists;
import dt.insight.plat.lang.base.JSONs;
import lombok.extern.slf4j.Slf4j;
import org.dozer.DozerBeanMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
public class Dozers {
    private Dozers() {
    }

    private static DozerBeanMapper mapper = new DozerBeanMapper();

    /**
     * 单个对象数据转换
     */
    public static <T> T convert(Object source, Class<T> destinationClass) {
        if (source == null) {
            return null;
        }
        return mapper.map(source, destinationClass);
    }

    /**
     * json 转换
     */
    public static <T> T convertJson(Object source, Class<T> destinationClass) throws IOException {
        if (source == null) {
            return null;
        }
        String json = JSONs.string(source);

        return JSONs.getObjectMapper().reader().readValue(json, destinationClass);
    }

    /**
     * 单个对象数据转换 + 回调
     *
     * @param source           源数据
     * @param destinationClass 目标类对象
     * @param callback         回调
     */
    public static <T, S> T convert(S source, Class<T> destinationClass, ConvertCallBack<T, S> callback) {
        if (source == null) {
            return null;
        }
        T target = mapper.map(source, destinationClass);
        if (callback != null) {
            callback.callback(target, source, destinationClass);
        }
        return target;
    }


    /**
     * 列表数据转换
     */
    public static <T, S> List<T> convertList(List<S> sourceList, Class<T> destinationClass) {
        if (sourceList != null && sourceList.size() > 0) {
            List<T> retList = new ArrayList<>();
            for (S source : sourceList) {
                retList.add(mapper.map(source, destinationClass));
            }
            return retList;
        }
        return Collections.emptyList();
    }

    /**
     * 列表数据转换 + 回调
     *
     * @param sourceList       源数据列表
     * @param destinationClass 目标类对象
     * @param callback         回调
     */
    public static <T, S> List<T> convertList(List<S> sourceList, Class<T> destinationClass, ConvertCallBackList<T, S> callback) {
        if (sourceList != null && sourceList.size() > 0) {
            List<T> retList = new ArrayList<>();
            for (S source : sourceList) {
                T target = mapper.map(source, destinationClass);
                if (callback != null) {
                    callback.callbackForList(retList, target, source, destinationClass);
                }
            }
            return retList;
        }
        return Lists.newArrayList();
    }

    public interface ConvertCallBack<T, S> {
        void callback(T element, S source, Class clazz);
    }

    public interface ConvertCallBackList<T, S>  extends java.io.Serializable {
        void callbackForList(List<T> list, T element, S source, Class clazz);
    }

}

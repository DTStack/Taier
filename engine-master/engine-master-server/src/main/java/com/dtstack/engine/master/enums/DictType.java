package com.dtstack.engine.master.enums;

import com.dtstack.engine.common.enums.EComponentType;

/**
 * @author yuebai
 * @date 2021-03-02
 * 0～4 各个组件对于版本
 * 5 各个版本组件的额外配置
 * 6 默认模版id和typename对应关系
 */
public enum DictType {
    HADOOP_VERSION(0),
    FLINK_VERSION(1),
    SPARK_VERSION(2),
    SPARK_THRIFT_VERSION(3),
    HIVE_VERSION(4),
    COMPONENT_CONFIG(5),
    TYPENAME_MAPPING(6),
    DATA_CLEAR_NAME(8);

    public Integer type;

    DictType(Integer type) {
        this.type = type;
    }

    public static Integer getByEComponentType(EComponentType type) {
        switch (type) {
            case FLINK:
                return FLINK_VERSION.type;
            case SPARK:
                return SPARK_VERSION.type;
            case HIVE_SERVER:
                return HIVE_VERSION.type;
            case SPARK_THRIFT:
                return SPARK_THRIFT_VERSION.type;
            default:
                return null;
        }
    }
}

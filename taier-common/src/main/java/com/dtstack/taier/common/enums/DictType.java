/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dtstack.taier.common.enums;

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
    COMPONENT_MODEL(12),
    RESOURCE_MODEL_CONFIG(14),
    EXTRA_VERSION_TEMPLATE(15),
    HDFS_TYPE_NAME(16),

    SYNC_MAPPING(17),
    TIPS(25),

    TASK_TYPE_PROPERTIES(30),
    DATA_DEVELOP_CATALOGUE_L1(31),
    DATA_DEVELOP_CATALOGUE(32),
    DATA_DEVELOP_FUNCTION(33),
    SCRIPT_VERSION(34),

    ;

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
            default:
                return null;
        }
    }

    public Integer getType() {
        return type;
    }
}

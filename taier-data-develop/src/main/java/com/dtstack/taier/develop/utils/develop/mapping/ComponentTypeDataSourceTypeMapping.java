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

package com.dtstack.taier.develop.utils.develop.mapping;

import com.dtstack.dtcenter.loader.source.DataSourceType;
import com.dtstack.taier.common.enums.EComponentType;
import com.dtstack.taier.common.exception.RdosDefineException;
import com.google.common.collect.Maps;

import java.util.Map;

/**
 * @author tz
 * @description 组件和数据源类型枚举对应关系类
 * @date 2021/4/1 2:56 下午
 */
public class ComponentTypeDataSourceTypeMapping {

    private final static Map<Integer, EComponentType> mappingMap = Maps.newHashMap();

    static {
        mappingMap.put(DataSourceType.SparkThrift2_1.getVal(), EComponentType.SPARK_THRIFT);
        mappingMap.put(DataSourceType.Spark.getVal(), EComponentType.SPARK);
        mappingMap.put(DataSourceType.HIVE.getVal(), EComponentType.HIVE_SERVER);
        mappingMap.put(DataSourceType.HIVE1X.getVal(), EComponentType.HIVE_SERVER);
        mappingMap.put(DataSourceType.HIVE3X.getVal(), EComponentType.HIVE_SERVER);
    }


    /**
     * 获取 根据dataSourceType 获取EComponentType值
     *
     * @param dataSourceType
     * @return
     */
    public static Integer getEComponentType(Integer dataSourceType){
        EComponentType eComponentType = mappingMap.get(dataSourceType);
        if(eComponentType == null){
            throw new RdosDefineException("无法通过dataSourceType获取组件code");
        }
        return eComponentType.getTypeCode();
    }
}

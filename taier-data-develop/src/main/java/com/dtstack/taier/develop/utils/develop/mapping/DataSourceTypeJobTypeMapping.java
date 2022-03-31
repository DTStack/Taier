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
import com.dtstack.taier.common.enums.EScheduleJobType;
import com.dtstack.taier.common.exception.RdosDefineException;
import com.google.common.collect.Maps;

import java.util.Map;
import java.util.Objects;

/**
 * @author <a href="mailto:jiangyue@dtstack.com">江月 At 袋鼠云</a>.
 * @description
 * @date 2021/4/1 2:56 下午
 */
public class DataSourceTypeJobTypeMapping {

    private final static Map<Integer, EScheduleJobType> mappingMap = Maps.newHashMap();

    static {
        mappingMap.put(DataSourceType.SparkThrift2_1.getVal(), EScheduleJobType.SPARK_SQL);
        mappingMap.put(DataSourceType.HIVE.getVal(), EScheduleJobType.HIVE_SQL);
        mappingMap.put(DataSourceType.HIVE1X.getVal(), EScheduleJobType.HIVE_SQL);
        mappingMap.put(DataSourceType.HIVE3X.getVal(), EScheduleJobType.HIVE_SQL);
    }

    /**
     * 获取 根据dataSourceType 获取jobType
     *
     * @param dataSourceType
     * @return
     */
    public static EScheduleJobType getTaskTypeByDataSourceType(Integer dataSourceType){
        EScheduleJobType eScheduleJobType = mappingMap.get(dataSourceType);
        if (Objects.isNull(eScheduleJobType)) {
            throw new RdosDefineException("无法通过dataSourceType获取EScheduleJobType");
        }
        return eScheduleJobType;
    }
}

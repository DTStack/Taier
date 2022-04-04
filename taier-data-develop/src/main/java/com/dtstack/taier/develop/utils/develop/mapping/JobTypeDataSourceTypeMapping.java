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
import com.dtstack.taier.common.enums.HiveVersion;
import com.dtstack.taier.common.exception.RdosDefineException;
import com.google.common.collect.Maps;

import java.util.Map;

public class JobTypeDataSourceTypeMapping {

    private final static Map<Integer, Integer> mappingMap = Maps.newHashMap();

    static {
        mappingMap.put(EScheduleJobType.SPARK_SQL.getVal(), DataSourceType.SparkThrift2_1.getVal());
        mappingMap.put(EScheduleJobType.HIVE_SQL.getVal(), DataSourceType.HIVE.getVal());
        mappingMap.put(EScheduleJobType.HIVE_SQL.getVal(), DataSourceType.HIVE1X.getVal());
        mappingMap.put(EScheduleJobType.HIVE_SQL.getVal(), DataSourceType.HIVE3X.getVal());
    }

    /**
     * 根据任务类型、版本信息  获取数据源类型
     * @param jobType
     * @param version
     * @return
     */
    public static Integer getDataSourceTypeByJobType(Integer jobType, String version) {
        Integer dataSourceType = mappingMap.get(jobType);
        if (EScheduleJobType.HIVE_SQL.getVal().equals(jobType)) {
            if (HiveVersion.HIVE_1x.getVersion().equals(version)) {
                return DataSourceType.HIVE1X.getVal();
            } else if (HiveVersion.HIVE_3x.getVersion().equals(version)) {
                return DataSourceType.HIVE3X.getVal();
            } else {
                return DataSourceType.HIVE.getVal();
            }
        }
        if (dataSourceType == null) {
            throw new RdosDefineException("无法通过jobType获取dataSourceType");
        }
        return dataSourceType;
    }
}

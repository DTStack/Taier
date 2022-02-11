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

package com.dtstack.taiga.develop.utils.develop.mapping;

import com.dtstack.taiga.common.enums.EComponentType;
import com.dtstack.taiga.common.enums.EScheduleJobType;
import com.dtstack.taiga.common.exception.RdosDefineException;
import com.google.common.collect.Maps;

import java.util.Map;

public class EScheduleJobTypeToComponentTypeMapping {

    private final static Map<EScheduleJobType, EComponentType> MAPPING_MAP = Maps.newHashMap();

    static {
        // 因为SPARK_SQL的版本依赖于Spark组件，而非SparkThrift组件
        MAPPING_MAP.put(EScheduleJobType.SPARK_SQL, EComponentType.SPARK);

        MAPPING_MAP.put(EScheduleJobType.SYNC, EComponentType.FLINK);
    }

    /**
     * 获取 componentCode 获取EScheduleJobType值
     *
     * @param taskType 任务类型
     * @return
     */
    public static EComponentType getEScheduleTypeByComponentCode(Integer taskType) {
        EScheduleJobType scheduleJobType = EScheduleJobType.getByTaskType(taskType);
        if (!MAPPING_MAP.containsKey(scheduleJobType)) {
            throw new RdosDefineException(String.format("无法通过typeCode[%s]获取EComponentType", taskType));
        }
        return MAPPING_MAP.get(scheduleJobType);
    }
}

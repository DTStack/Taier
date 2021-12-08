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

package com.dtstack.batch.mapping;

import com.dtstack.dtcenter.common.enums.EJobType;
import com.dtstack.dtcenter.common.enums.MultiEngineType;
import com.google.common.collect.Maps;

import java.util.Map;

/**
 * TODO 暂时这么写,考虑是否有更好的方式方便之后新增类型不用修改代码
 * 任务类型和引擎类型直接的映射关系
 * Date: 2019/5/14
 * Company: www.dtstack.com
 * @author xuchao
 */

public class TaskTypeEngineTypeMapping {

    private final static Map<Integer, MultiEngineType> REF_MAP = Maps.newHashMap();

    static {
        REF_MAP.put(EJobType.SPARK_SQL.getVal(), MultiEngineType.HADOOP);
        REF_MAP.put(EJobType.SPARK.getVal(), MultiEngineType.HADOOP);
        REF_MAP.put(EJobType.SPARK_PYTHON.getVal(), MultiEngineType.HADOOP);
        REF_MAP.put(EJobType.R.getVal(), MultiEngineType.HADOOP);
        REF_MAP.put(EJobType.SYNC.getVal(), MultiEngineType.HADOOP);
        REF_MAP.put(EJobType.PYTHON.getVal(), MultiEngineType.HADOOP);
        REF_MAP.put(EJobType.SHELL.getVal(), MultiEngineType.HADOOP);
        REF_MAP.put(EJobType.SHELL_ON_AGENT.getVal(), MultiEngineType.HADOOP);
        REF_MAP.put(EJobType.ML_LIb.getVal(), MultiEngineType.HADOOP);
        REF_MAP.put(EJobType.HADOOP_MR.getVal(), MultiEngineType.HADOOP);
        REF_MAP.put(EJobType.CARBON_SQL.getVal(), MultiEngineType.HADOOP);
        REF_MAP.put(EJobType.ALGORITHM_LAB.getVal(), MultiEngineType.HADOOP);

        REF_MAP.put(EJobType.LIBRA_SQL.getVal(), MultiEngineType.LIBRA);
        REF_MAP.put(EJobType.HIVE_SQL.getVal(), MultiEngineType.HADOOP);
        REF_MAP.put(EJobType.IMPALA_SQL.getVal(), MultiEngineType.HADOOP);
        REF_MAP.put(EJobType.INCEPTOR_SQL.getVal(), MultiEngineType.HADOOP);
        REF_MAP.put(EJobType.TIDB_SQL.getVal(),MultiEngineType.TIDB);
        REF_MAP.put(EJobType.ORACLE_SQL.getVal(),MultiEngineType.ORACLE);
        REF_MAP.put(EJobType.GREENPLUM_SQL.getVal(),MultiEngineType.GREENPLUM);
        REF_MAP.put(EJobType.ANALYTICDB_FOR_PG.getVal(),MultiEngineType.ANALYTICDB_FOR_PG);
    }

    public static MultiEngineType getEngineTypeByTaskType(Integer taskType){
        return REF_MAP.get(taskType);
    }
}

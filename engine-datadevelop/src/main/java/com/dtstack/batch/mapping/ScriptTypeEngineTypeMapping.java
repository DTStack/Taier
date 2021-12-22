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

import com.dtstack.engine.common.enums.EScriptType;
import com.dtstack.engine.common.enums.EngineType;
import com.dtstack.engine.common.enums.MultiEngineType;
import com.google.common.collect.Maps;

import java.util.Map;

/**
 * 脚本类型和引擎类型直接的映射关系
 * Date: 2019/5/20
 * Company: www.dtstack.com
 *
 * @author xuchao
 */

public class ScriptTypeEngineTypeMapping {

    private final static Map<Integer, MultiEngineType> refMap = Maps.newHashMap();
    private final static Map<Integer, EngineType> jobTypeMap = Maps.newHashMap();

    static {
        refMap.put(EScriptType.SparkSQL.getType(), MultiEngineType.HADOOP);
        refMap.put(EScriptType.Python_2x.getType(), MultiEngineType.HADOOP);
        refMap.put(EScriptType.Python_3x.getType(), MultiEngineType.HADOOP);
        refMap.put(EScriptType.Shell.getType(), MultiEngineType.HADOOP);
        refMap.put(EScriptType.GaussDBSQL.getType(), MultiEngineType.LIBRA);
        refMap.put(EScriptType.ImpalaSQL.getType(), MultiEngineType.HADOOP);

        jobTypeMap.put(EScriptType.SparkSQL.getType(), EngineType.Spark);
        jobTypeMap.put(EScriptType.Python_2x.getType(), EngineType.Python2);
        jobTypeMap.put(EScriptType.Python_3x.getType(), EngineType.Python3);
        jobTypeMap.put(EScriptType.Shell.getType(), EngineType.Shell);
        jobTypeMap.put(EScriptType.GaussDBSQL.getType(), EngineType.GaussDB);
    }

    public static MultiEngineType getEngineTypeByTaskType(Integer taskType) {
        return refMap.get(taskType);
    }

    public static EngineType getEngineTypeByScriptType(Integer scriptType) {
        return jobTypeMap.get(scriptType);
    }
}

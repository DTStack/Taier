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

import com.dtstack.batch.common.enums.ETableType;
import com.dtstack.batch.common.exception.RdosDefineException;
import com.dtstack.dtcenter.common.enums.MultiEngineType;
import com.google.common.collect.Maps;

import java.util.Map;

/**
 * Reason:
 * Date: 2019/5/30
 * Company: www.dtstack.com
 *
 * @author xuchao
 */

public class TableTypeEngineTypeMapping {

    private final static Map<Integer, MultiEngineType> REF_MAP = Maps.newHashMap();

    static {
        REF_MAP.put(ETableType.HIVE.getType(), MultiEngineType.HADOOP);
        REF_MAP.put(ETableType.IMPALA.getType(), MultiEngineType.HADOOP);
        REF_MAP.put(ETableType.LIBRA.getType(), MultiEngineType.LIBRA);
        REF_MAP.put(ETableType.TIDB.getType(), MultiEngineType.TIDB);
        REF_MAP.put(ETableType.ORACLE.getType(), MultiEngineType.ORACLE);
        REF_MAP.put(ETableType.GREENPLUM.getType(), MultiEngineType.GREENPLUM);
        REF_MAP.put(ETableType.ADB_FOR_PG.getType(), MultiEngineType.ANALYTICDB_FOR_PG);
    }

    public static MultiEngineType getEngineTypeByTableType(Integer tableType) {
        MultiEngineType multiEngineType = REF_MAP.get(tableType);
        if (multiEngineType == null) {
            throw new RdosDefineException(String.format("根据表类型：%s，获取引擎类型失败", tableType));
        }
        return multiEngineType;
    }

    public static ETableType getTableTypeByEngineType(MultiEngineType engineType) {
        return getTableTypeByEngineType(null == engineType ? null : engineType.getType());
    }


    public static ETableType getTableTypeByEngineType(Integer engineType) {
        if (MultiEngineType.HADOOP.getType() == engineType) {
            return ETableType.HIVE;
        }
        if (MultiEngineType.LIBRA.getType() == engineType) {
            return ETableType.LIBRA;
        }
        if (MultiEngineType.TIDB.getType() == engineType) {
            return ETableType.TIDB;
        }
        if (MultiEngineType.ORACLE.getType() == engineType) {
            return ETableType.ORACLE;
        }
        if (MultiEngineType.GREENPLUM.getType() == engineType) {
            return ETableType.GREENPLUM;
        }
        if (MultiEngineType.ANALYTICDB_FOR_PG.getType() == engineType) {
            return ETableType.ADB_FOR_PG;
        }
        return null;
    }
}

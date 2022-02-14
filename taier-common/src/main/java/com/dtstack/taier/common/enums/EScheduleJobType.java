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

import com.dtstack.taier.common.exception.RdosDefineException;

public enum EScheduleJobType {

    VIRTUAL(-1, "虚节点", -1, 0, null),
    SPARK_SQL(0, "SparkSQL", 0, 1, EComponentType.SPARK),
    SPARK(1, "Spark", 1, 2, EComponentType.SPARK),
    SYNC(2, "数据同步", 2, 3, EComponentType.FLINK),
    SHELL(3, "Shell", 2, 3, null),
    WORK_FLOW(10, "工作流", -1, 9, null),
    ;


    private Integer type;

    private String name;

    /**
     * 引擎能够接受的jobType
     * SQL              0
     * MR               1
     * SYNC             2
     * PYTHON           3
     * 不接受的任务类型    -1
     */
    private Integer engineJobType;

    private Integer sort;

    private EComponentType componentType;


    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getEngineJobType() {
        return engineJobType;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public EComponentType getComponentType() {
        return componentType;
    }


    EScheduleJobType(Integer type, String name, Integer engineJobType, Integer sort, EComponentType componentType) {
        this.type = type;
        this.name = name;
        this.engineJobType = engineJobType;
        this.sort = sort;
        this.componentType = componentType;
    }

    public Integer getVal() {
        return this.type;
    }

    public String getName() {
        return name;
    }


    public static EScheduleJobType getByTaskType(int type) {
        EScheduleJobType[] eJobTypes = EScheduleJobType.values();
        for (EScheduleJobType eJobType : eJobTypes) {
            if (eJobType.type == type) {
                if (eJobType.getVal() != -1) {
                    return eJobType;
                }
                break;
            }

        }
        throw new RdosDefineException("不支持的任务类型");
    }


}
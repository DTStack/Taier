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


public enum EngineCatalogueType {
    /**
     *
     */
    SPARK(-1, "SparkSQL"),

    FLINK(0, "FlinkSQL");

    private int type;

    private String desc;

    EngineCatalogueType(int type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public int getType() {
        return this.type;
    }

    public String getDesc() {
        return desc;
    }

    /**
     * 根据名称获取 EngineCatalogueType
     *
     * @param nodeName
     * @return
     */
    public static EngineCatalogueType getByeName(String nodeName) {
        for (EngineCatalogueType engineCatalogueType : EngineCatalogueType.values()) {
            if (engineCatalogueType.getDesc().equals(nodeName)) {
                return engineCatalogueType;
            }
        }

        return EngineCatalogueType.SPARK;
    }

    /**
     * 根据引擎类型获取目录结构
     *
     * @param taskType
     * @return
     */
    public static EngineCatalogueType getByComponentType(Integer taskType) {
        if (EComponentType.SPARK_THRIFT.getTypeCode().equals(taskType)) {
            return EngineCatalogueType.SPARK;
        }
        return null;
    }
}

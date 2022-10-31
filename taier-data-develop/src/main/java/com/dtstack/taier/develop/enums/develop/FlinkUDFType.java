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

package com.dtstack.taier.develop.enums.develop;

public enum FlinkUDFType {

    /**
     * flink scala  函数
     */
    SCALA(0, "SCALAR"),

    /**
     * flink table  函数
     */
    TABLE(1, "TABLE"),

    /**
     * flink aggregate 函数
     */
    AGGREGATE(2, "AGGREGATE");

    /**
     * 函数的类型
     */
    int type;

    /**
     * 函数名称
     */
    String name;

    FlinkUDFType(int type, String name) {
        this.type = type;
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public String getName() {
        return name;
    }


    public static FlinkUDFType fromTypeValue(int type) {
        for (FlinkUDFType flinkUDFType : FlinkUDFType.values()) {
            if (type == flinkUDFType.type) {
                return flinkUDFType;
            }
        }
        return SCALA;
    }
}

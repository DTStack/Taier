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

package com.dtstack.taier.common.metric.prometheus;

/**
 * 描述各个返回类型的数据格式
 * Date: 2018/10/10
 * Company: www.dtstack.com
 *
 * @author xuchao
 */
public enum MetricResultType {

    /**
     * [
     * {
     * "metric": { "<label_name>": "<label_value>", ... },
     * "values": [ [ <unix_time>, "<sample_value>" ], ... ]
     * },
     * ...
     * ]
     */
    MATRIX("matrix"),


    /**
     * [
     * {
     * "metric": { "<label_name>": "<label_value>", ... },
     * "value": [ <unix_time>, "<sample_value>" ]
     * },
     * ...
     * ]
     */
    VERTOR("vector"),


    /**
     * [ <unix_time>, "<scalar_value>" ]
     */
    SCALAR("scalar"),


    /**
     * [ <unix_time>, "<string_value>" ]
     */
    TRING("string");

    private String typeInfo;

    MetricResultType(String typeInfo) {
        this.typeInfo = typeInfo;
    }

    public String getTypeInfo() {
        return typeInfo;
    }

}

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

package com.dtstack.taier.develop.dto.devlop;


import java.io.Serializable;
import java.util.List;
import java.util.Map;


public class FlinkSqlRuntimeLogDTO implements Serializable {

    /**
     * 日志类型:两种 taskmanager | jobmanager
     */
    private String typeName;

    /**
     * 日志滚动路径等信息
     */
    private List<Map<String, Object>> logs;

    /**
     * 日志类型如果是 taskmanager会有这个参数，用于获取taskmanager相关信息
     */
    private String otherInfo;

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public List<Map<String, Object>> getLogs() {
        return logs;
    }

    public void setLogs(List<Map<String, Object>> logs) {
        this.logs = logs;
    }

    public String getOtherInfo() {
        return otherInfo;
    }

    public void setOtherInfo(String otherInfo) {
        this.otherInfo = otherInfo;
    }


}
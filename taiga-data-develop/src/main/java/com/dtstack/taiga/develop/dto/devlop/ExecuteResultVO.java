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


package com.dtstack.taiga.develop.dto.devlop;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author jiangbo
 * @date 2019/6/14
 */
@Data
@NoArgsConstructor
public class ExecuteResultVO<T> {

    private String jobId;

    private String sqlText;

    private String msg;

    private Integer status;

    private List<T> result;

    private Boolean isContinue = false;

    private String download;

    private Integer taskType;

    private Boolean retryLog;

    public ExecuteResultVO(String jobId) {
        this.jobId = jobId;
    }

}

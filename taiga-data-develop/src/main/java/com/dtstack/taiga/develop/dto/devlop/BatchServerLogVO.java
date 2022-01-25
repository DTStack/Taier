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

import java.sql.Timestamp;
import java.util.Map;

/**
 * @author jiangbo
 */
@Data
public class BatchServerLogVO {

    private String name;
    private String logInfo;
    private Timestamp execStartTime;
    private Timestamp execEndTime;
    private Integer taskType = 0;
    private Integer computeType = 0;
    private SyncJobInfo syncJobInfo;
    private String downloadLog;
    private Map<String, String> subNodeDownloadLog;
    //经过几次任务重试
    private Integer pageSize;
    //当前页
    private Integer pageIndex;

    @Data
    public static class SyncJobInfo{

        private Integer readNum = 0;

        private Integer writeNum = 0;

        private Float dirtyPercent = 0.0F;

        private Long execTime = 0L;
    }

}

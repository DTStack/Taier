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

package com.dtstack.taier.dao.domain;

/**
 * metric 指标及含义
 *
 * @author ：wangchuan
 * date：Created in 上午11:35 2021/4/16
 * company: www.dtstack.com
 */
public class StreamMetricSupport extends BaseEntity {

    // 指标中文名称
    private String name;

    // 指标支持的任务类型
    private String taskType;

    // 指标 key
    private String value;

    // 指标过滤的字段
    private Integer metricTag;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTaskType() {
        return taskType;
    }

    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Integer getMetricTag() {
        return metricTag;
    }

    public void setMetricTag(Integer metricTag) {
        this.metricTag = metricTag;
    }
}

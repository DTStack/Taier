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

package com.dtstack.taiga.develop.web.develop.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.sql.Timestamp;

@Data
@ApiModel("函数任务信息")
public class BatchFunctionTaskVO {
    @ApiModelProperty(value = "任务名称", example = "spark_test")
    private String name;

    @ApiModelProperty(value = "任务类型 0 sql，1 mr，2 sync ，3 python", example = "1")
    private Integer taskType;

    @ApiModelProperty(value = "计算类型 0实时，1 离线", example = "1")
    private Integer computeType;

    @ApiModelProperty(value = "执行引擎类型 0 flink, 1 spark", example = "0")
    private Integer engineType;

    @ApiModelProperty(value = "sql 文本", example = "shwo tables;")
    private String sqlText;

    @ApiModelProperty(value = "任务参数")
    private String taskParams;

    @ApiModelProperty(value = "调度配置", example = "{\"selfReliance\":false}")
    private String scheduleConf;

    @ApiModelProperty(value = "周期类型", example = "1")
    private Integer periodType;

    @ApiModelProperty(value = "调度状态", example = "0")
    private Integer scheduleStatus;

    @ApiModelProperty(value = "提交状态", example = "1")
    private Integer submitStatus;

    @ApiModelProperty(value = "任务发布状态，前端使用", example = "spark_test")
    private Integer status;

    @ApiModelProperty(value = "最后修改task的用户", example = "1")
    private Long modifyUserId;

    @ApiModelProperty(value = "新建task的用户", example = "3")
    private Long createUserId;

    @ApiModelProperty(value = "负责人id", example = "11")
    private Long ownerUserId;

    @ApiModelProperty(value = "task版本", example = "11")
    private Integer version;

    @ApiModelProperty(value = "节点父ID", example = "7")
    private Long nodePid;

    @ApiModelProperty(value = "任务描述", example = "测试")
    private String taskDesc;

    @ApiModelProperty(value = "入口类", example = "Abc.java")
    private String mainClass;

    @ApiModelProperty(value = "参数", example = "1,2")
    private String exeArgs;

    @ApiModelProperty(value = "所属工作流id", example = "1")
    private Long flowId = 0L;

    @ApiModelProperty(value = "是否过期", example = "0")
    private Integer isExpire;

    @ApiModelProperty(value = "租户 ID")
    private Long tenantId;

    @ApiModelProperty(value = "平台类别", example = "1")
    private Integer appType;

    @ApiModelProperty(value = "id")
    private Long id = 0L;

    @ApiModelProperty(value = "创建时间")
    private Timestamp gmtCreate;

    @ApiModelProperty(value = "修改时间")
    private Timestamp gmtModified;

    @ApiModelProperty(value = "是否删除")
    private Integer isDeleted = 0;
}
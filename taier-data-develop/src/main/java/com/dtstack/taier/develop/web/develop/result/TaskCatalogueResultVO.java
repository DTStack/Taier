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

package com.dtstack.taier.develop.web.develop.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("任务目录信息")
public class TaskCatalogueResultVO{

    @ApiModelProperty(value = "调度状态", example = "1")
    private Integer scheduleStatus;

    @ApiModelProperty(value = "提交状态", example = "1")
    private Integer submitStatus;

    @ApiModelProperty(value = "目录信息")
    private List<? extends CatalogueResultVO> catalogues;

    @ApiModelProperty(value = "任务信息")
    private List<BatchTaskResultVO> tasks;

    @ApiModelProperty(value = "任务类别")
    private Integer taskType;

    @ApiModelProperty(value = "依赖任务")
    private List<BatchTaskResultVO> dependencyTasks;

    @ApiModelProperty(value = "列表信息")
    private List<List<Object>>  lists;

    @ApiModelProperty(value = "目录id", example = "1")
    private Long id = 0L;

    @ApiModelProperty(value = "父目录id", example = "0")
    private Long parentId = 0L;

    @ApiModelProperty(value = "目录名称", example = "name")
    private String name;

    @ApiModelProperty(value = "项目别名", example = "project_alia")
    private String projectAlias;

    @ApiModelProperty(value = "目录层级", example = "1")
    private Integer level;

    @ApiModelProperty(value = "目录类型", example = "folder")
    private String type;

    @ApiModelProperty(value = "资源类型")
    private Integer resourceType;

    @ApiModelProperty(value = "目录类型", example = "SystemFunction")
    private String catalogueType;

    @ApiModelProperty(value = "创建用户", example = "test")
    private String createUser;

    @ApiModelProperty(value = "节点值", example = "1")
    private Integer orderVal;

    @ApiModelProperty(value = "子目录列表")
    private List<BatchCatalogueResultVO> children;

    @ApiModelProperty(value = "读写锁")
    private ReadWriteLockVO readWriteLockVO;

    @ApiModelProperty(value = "版本", example = "1")
    private Integer version;

    @ApiModelProperty(value = "操作模式", example = "1")
    private Integer operateModel = 1;

    @ApiModelProperty(value = "python版本", example = "2")
    private Integer pythonVersion;

    @ApiModelProperty(value = "learning类型", example = "1")
    private Integer learningType;

    @ApiModelProperty(value = "脚本类型", example = "1")
    private Integer scriptType;

    @ApiModelProperty(value = "是否为子任务", example = "0")
    private Integer isSubTask = 0;

    @ApiModelProperty(value = "engine类型", example = "1")
    private Integer engineType;

    @ApiModelProperty(value = "任务状态", example = "1")
    private Integer status;

}

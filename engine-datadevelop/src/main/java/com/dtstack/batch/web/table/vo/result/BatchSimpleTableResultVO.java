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

package com.dtstack.batch.web.table.vo.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.sql.Timestamp;

@Data
@ApiModel("表基本信息 单纯的表信息 table_info")
public class BatchSimpleTableResultVO {

    /**
     * 表id
     */
    @ApiModelProperty(value = "表id", example = "1")
    private Long id;

    /**
     * 表名称
     */
    @ApiModelProperty(value = "表名称", example = "1")
    private String tableName;

    /***
     * 表类型
     */
    @ApiModelProperty(value = "表类型", example = "1")
    private Integer tableType;

    /**
     * 创建表的用户id
     */
    @ApiModelProperty(value = "创建表的用户id", example = "1")
    private Long userId;

    /**
     * 表负责人
     */
    @ApiModelProperty(value = "表负责人", example = "1")
    private Long chargeUserId;

    @ApiModelProperty(value = "表修改人", example = "1")
    private Long modifyUserId;

    /**
     * 表大小
     */
    @ApiModelProperty(value = "表大小", example = "1")
    private Long tableSize;

    /**
     * 表下文件数量
     */
    @ApiModelProperty(value = "表下文件数量", example = "1")
    private Long tableFileCount;

    /**
     * 表大小更新时间
     */
    @ApiModelProperty(value = "表大小更新时间", example = "1")
    private Timestamp sizeUpdateTime;

    /**
     * 类目id
     */
    @ApiModelProperty(value = "类目id", example = "1")
    private Long catalogueId;

    /**
     * 类目路径
     */
    @ApiModelProperty(value = "类目路径", example = "1")
    private String path;

    /**
     * hdfs路径
     */
    @ApiModelProperty(value = "hdfs路径", example = "1")
    private String location;

    /**
     * 列分隔符
     */
    @ApiModelProperty(value = "列分隔符", example = "1")
    private String delim;

    /**
     * 存储格式
     */
    @ApiModelProperty(value = "存储格式", example = "1")
    private String storeType;

    /**
     * 生命周期，单位：day
     */
    @ApiModelProperty(value = "生命周期，单位：day", example = "1")
    private Integer lifeDay;

    /**
     * 生命周期状态，0：未开始，1：存活，2：销毁，3：执行过程出现异常
     */
    @ApiModelProperty(value = "表类型", example = "1")
    private Integer lifeStatus;

    /**
     * 是否是脏数据表 0-否，1-是
     */
    @ApiModelProperty(value = "是否是脏数据表 0-否，1-是", example = "1")
    private Integer isDirtyDataTable = 0;

    /**
     * 表结构最近修改时间
     */
    @ApiModelProperty(value = "表结构最近修改时间", example = "1")
    private Timestamp lastDdlTime;

    /**
     * 表数据最后修改时间
     */
    @ApiModelProperty(value = "表数据最后修改时间", example = "1")
    private Timestamp lastDmlTime;

    /**
     * 表描述
     */
    @ApiModelProperty(value = "表描述", example = "1")
    private String tableDesc;

    /**
     * 层级
     */
    @ApiModelProperty(value = "层级", example = "1")
    private String grade;

    /**
     * 主题域
     */
    @ApiModelProperty(value = "主题域", example = "1")
    private String subject;

    /**
     * 刷新频率
     */
    @ApiModelProperty(value = "刷新频率", example = "1")
    private String refreshRate;

    /**
     * 增量类型
     */
    @ApiModelProperty(value = "增量类型", example = "1")
    private String increType;

    /**
     * 是否忽略
     */
    @ApiModelProperty(value = "是否忽略 0否1是", example = "0")
    private Integer isIgnore;

    @ApiModelProperty(value = "校验结果", example = "1")
    private String checkResult;

    /**
     * 是否分区
     */
    @ApiModelProperty(value = "是否分区 0否1是", example = "1")
    private Integer isPartition;
}

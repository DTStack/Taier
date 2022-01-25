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
@ApiModel("读写锁信息")
public class ReadWriteLockVO {
    @ApiModelProperty(value = "上一个持有锁的用户名", example = "admin")
    private String lastKeepLockUserName;

    @ApiModelProperty(value = "检查结果", example = "0")
    private Integer result = 0;

    @ApiModelProperty(value = "是否持有锁", example = "false")
    private Boolean getLock = false;

    @ApiModelProperty(value = "锁名称", example = "锁")
    private String lockName;

    @ApiModelProperty(value = "修改的用户", example = "1")
    private Long modifyUserId;

    @ApiModelProperty(value = "版本", example = "0")
    private Integer version;

    @ApiModelProperty(value = "任务Id", example = "0")
    private Long relationId;

    @ApiModelProperty(value = "任务类型", example = "sql")
    private String type;

    @ApiModelProperty(value = "读写锁id")
    private Long id = 0L;

    @ApiModelProperty(value = "创建时间", example = "2020-08-14 14:41:55")
    private Timestamp gmtCreate;

    @ApiModelProperty(value = "修改时间", example = "2020-08-14 14:41:55")
    private Timestamp gmtModified;

    @ApiModelProperty(value = "是否删除", example = "0")
    private Integer isDeleted = 0;
}

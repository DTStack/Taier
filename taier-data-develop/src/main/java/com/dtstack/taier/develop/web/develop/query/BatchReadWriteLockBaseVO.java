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

package com.dtstack.taier.develop.web.develop.query;

import com.dtstack.taier.common.param.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.sql.Timestamp;

@Data
@ApiModel("读写锁信息")
public class BatchReadWriteLockBaseVO extends DtInsightAuthParam {

    @ApiModelProperty(value = "上一个持有锁的用户名", example = "1")
    private String lastKeepLockUserName;

    @ApiModelProperty(value = "检查结果", example = "1")
    private Integer result = 0;

    @ApiModelProperty(value = "是否持有锁", hidden = true)
    private Boolean isGetLock = false;

    @ApiModelProperty(value = "锁名称", hidden = true)
    private String lockName;

    @ApiModelProperty(value = "修改的用户", hidden = true)
    private Long modifyUserId;

    @ApiModelProperty(value = "乐观锁", example = "1")
    private Integer version;

    @ApiModelProperty(value = "项目 ID", hidden = true)
    private Long projectId;

    @ApiModelProperty(value = "任务 ID", example = "1", required = true)
    private Long relationId;

    @ApiModelProperty(value = "任务类型", example = "1", required = true)
    private String type;

    @ApiModelProperty(value = "ID", hidden = true)
    private Long id = 0L;

    @ApiModelProperty(value = "创建时间", hidden = true)
    private Timestamp gmtCreate;

    @ApiModelProperty(value = "修改时间", hidden = true)
    private Timestamp gmtModified;

    @ApiModelProperty(value = "是否删除", hidden = true)
    private Integer isDeleted = 0;

}

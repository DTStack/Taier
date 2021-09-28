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

package com.dtstack.batch.web.project.vo.query;

import com.dtstack.batch.web.user.vo.query.BatchUserBaseVO;
import com.dtstack.engine.common.param.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Data
@ApiModel("项目添加信息")
public class BatchProjectCreateVO extends DtInsightAuthParam {

    @ApiModelProperty(value = "用户 ID", hidden = true)
    private Long userId;

    @ApiModelProperty(value = "UIC 租户 ID", hidden = true)
    private Long dtuicTenantId;

    @ApiModelProperty(value = "项目 ID", hidden = true)
    private Long projectId;

    @ApiModelProperty(value = "租户 ID", hidden = true)
    private Long tenantId;

    @ApiModelProperty(value = "token", hidden = true)
    private String dtToken;

    @ApiModelProperty(value = "项目描述", example = "这是描述")
    private String projectDesc;

    @ApiModelProperty(value = "项目别名", example = "这是别名")
    private String projectAlias;

    @ApiModelProperty(value = "创建用户")
    private BatchUserBaseVO createUser;

    @ApiModelProperty(value = "admin用户")
    private List<BatchUserBaseVO> adminUsers;

    @ApiModelProperty(value = "游客用户")
    private List<BatchUserBaseVO> memberUsers;

    @ApiModelProperty(value = "生产项目", example = "生产项目", required = true)
    private String produceProject;

    @ApiModelProperty(value = "测试项目", example = "测试项目", required = true)
    private String testProject;

    @ApiModelProperty(value = "测试项目Id", example = "1L", required = true)
    private Long testProjectId;

    @ApiModelProperty(value = "项目支持引擎", required = true)
    private List<BatchProjectEngineBaseVO> projectEngineList = new ArrayList<>();

    @ApiModelProperty(value = "项目标识", example = "标识", required = true)
    private String projectIdentifier;

    @ApiModelProperty(value = "项目名称", example = "若木的项目", required = true)
    private String projectName;

    @ApiModelProperty(value = "项目状态", example = "1", required = true)
    private Integer status;

    @ApiModelProperty(value = "创建项目用户 ID", example = "1L", required = true)
    private Long createUserId;

    @ApiModelProperty(value = "项目类型", example = "1", required = true)
    private Integer projectType;

    @ApiModelProperty(value = "生产项目 ID", example = "1L", required = true)
    private Long produceProjectId;

    @ApiModelProperty(value = "调度状态", example = "1", required = true)
    private Integer scheduleStatus;

    @ApiModelProperty(value = "是否允许下载查询结果", example = "1-正常 0-禁用", required = true)
    private Integer isAllowDownload;

    @ApiModelProperty(value = "项目创建人", example = "admin", required = true)
    private String createUserName;

    @ApiModelProperty(value = "目录 ID", example = "1L", required = true)
    private Long catalogueId;

    @ApiModelProperty(value = "告警状态", example = "1", required = true)
    private Integer alarmStatus;

    @ApiModelProperty(value = "项目 ID", example = "0")
    private Long id;

    @ApiModelProperty(value = "创建时间", example = "2020-12-23 11:42:14")
    private Timestamp gmtCreate;

    @ApiModelProperty(value = "修改时间", example = "2020-12-23 11:42:14")
    private Timestamp gmtModified;

    @ApiModelProperty(value = "是否删除", example = "1")
    private Integer isDeleted = 0;
}

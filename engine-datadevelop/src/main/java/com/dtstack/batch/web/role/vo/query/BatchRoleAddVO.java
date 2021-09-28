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

package com.dtstack.batch.web.role.vo.query;

import com.dtstack.engine.common.param.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

@Data
@ApiModel("添加角色信息")
public class BatchRoleAddVO extends DtInsightAuthParam {

    @ApiModelProperty(hidden = true)
    private Long tenantId;

    @ApiModelProperty(hidden = true)
    private Long projectId;

    @ApiModelProperty(hidden = true)
    private Long userId;

    @ApiModelProperty(value = "权限ID")
    private List<Long> permissionIds;

    @ApiModelProperty(value = "修改的用户名", example = "admin")
    private String modifyUserName;

    @ApiModelProperty(value = "角色名称", example = "项目所有者")
    private String roleName;

    @ApiModelProperty(value = "角色类型", example = "1")
    private Integer roleType;

    @ApiModelProperty(value = "角色值", example = "1")
    private Integer roleValue;

    @ApiModelProperty(value = "角色排序", example = "desc")
    private String roleDesc;

    @ApiModelProperty(value = "修改用户ID", example = "0")
    private Long modifyUserId;

    @ApiModelProperty(value = "角色ID", example = "0")
    private Long id = 0L;

    @ApiModelProperty(value = "创建时间", example = "2020-11-04 14:48:05")
    private Timestamp gmtCreate;

    @ApiModelProperty(value = "修改时间", example = "2020-11-04 14:48:05")
    private Timestamp gmtModified;

    @ApiModelProperty(value = "是否删除", example = "0")
    private Integer isDeleted = 0;
}
